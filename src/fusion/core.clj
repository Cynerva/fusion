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
  (cond
    (seq? expr) (if (= `~(first expr) `deref)
                  `(let [ref# ~(replace-derefs watch-fn-sym (second expr))]
                     (lazy-watch ref# ~watch-fn-sym)
                     (deref ref# ~@(map (partial replace-derefs watch-fn-sym)
                                        (drop 2 expr))))
                    (map (partial replace-derefs watch-fn-sym) expr))
    (vector? expr) (mapv (partial replace-derefs watch-fn-sym) expr)
    (map? expr) (into {} (map (partial replace-derefs watch-fn-sym) expr))
    (set? expr) `(hash-set ~@(map (partial replace-derefs watch-fn-sym) expr))
    :else expr))

(defmacro fuse [& body]
  (let [watch-fn-sym (gensym "watch-fn")]
    `(let [fused# (fused-atom)]
       ((fn ~watch-fn-sym []
          (lazy-set! fused# ~@(map (partial replace-derefs watch-fn-sym)
                                   body))))
       fused#)))
