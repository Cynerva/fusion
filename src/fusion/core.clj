(ns fusion.core)

(defprotocol LazyWatchable
  (lazy-watch [this f]))

(extend-type clojure.lang.Atom
  LazyWatchable
  (lazy-watch [this f]
    (add-watch this f (fn [_ _ _ _] (f)))))

(deftype FusedAtom [state]
  clojure.lang.IDeref
  (deref [this] @@state)
  LazyWatchable
  (lazy-watch [this f]
    (add-watch state f (fn [_ _ _ _] (f)))))

(defn fused-atom []
  (FusedAtom. (atom (delay nil))))

(defmacro lazy-set! [fused & body]
  `(reset! (.state ~fused) (delay ~@body)))

(defn replace-derefs [watch-fn-sym expr]
  (let [replace-derefs (partial replace-derefs watch-fn-sym)]
    (cond
      (seq? expr) (if (= `~(first expr) `deref)
                    `(let [ref# ~(replace-derefs (second expr))]
                       (lazy-watch ref# ~watch-fn-sym)
                       (deref ref# ~@(map replace-derefs (drop 2 expr))))
                      (map replace-derefs expr))
      (vector? expr) (mapv replace-derefs expr)
      (map? expr) (into {} (map replace-derefs expr))
      (set? expr) `(hash-set ~@(map replace-derefs expr))
      :else expr)))

(defmacro fuse [& body]
  (let [watch-fn-sym (gensym "watch-fn")]
    `(let [fused# (fused-atom)]
       ((fn ~watch-fn-sym []
          (lazy-set! fused# ~@(map (partial replace-derefs watch-fn-sym)
                                   body))))
       fused#)))
