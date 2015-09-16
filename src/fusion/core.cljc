(ns fusion.core
  #?(:clj (:import [clojure.lang Atom IDeref])))

(defprotocol LazyWatchable
  (lazy-watch [this key f]))

(extend-type Atom
  LazyWatchable
  (lazy-watch [this key f]
    (add-watch this key (fn [_ _ _ _] (f)))))

(deftype FusedAtom [f state]
  IDeref
  (#?(:clj deref :cljs -deref) [this]
    @@state)
  LazyWatchable
  (lazy-watch [this key f]
    (add-watch state key (fn [_ _ _ _] (f)))))

(defn dirty-fused! [fused]
  (reset! (.-state fused)
          (delay ((.-f fused)
                  fused))))

(defn make-fused-atom [f]
  (let [fused (FusedAtom. f (atom nil))]
    (dirty-fused! fused)
    fused))

#?(:clj
(defn- replace-derefs [fused-sym expr]
  (let [replace-derefs (partial replace-derefs fused-sym)]
    (cond
      (seq? expr) (if (= `~(first expr) `deref)
                    `(let [ref# ~(replace-derefs (second expr))]
                       (lazy-watch ref# ~fused-sym #(dirty-fused! ~fused-sym))
                       (deref ref# ~@(map replace-derefs (drop 2 expr))))
                    (map replace-derefs expr))
      (vector? expr) (mapv replace-derefs expr)
      (map? expr) (into {} (map replace-derefs expr))
      (set? expr) `(hash-set ~@(map replace-derefs expr))
      :else expr))))

#?(:clj
(defmacro fuse [& body]
  (let [fused-sym (gensym "fused")]
    `(make-fused-atom (fn [~fused-sym]
                        ~@(map (partial replace-derefs fused-sym)
                               body))))))
