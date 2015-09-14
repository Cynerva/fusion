(ns fusion.core)

(def ^:dynamic *watcher*)

(defprotocol LazyWatchable
  (lazy-watch [this f]))

(extend-type clojure.lang.Atom
  LazyWatchable
  (lazy-watch [this f]
    (add-watch this f (fn [_ _ _ _] (f)))))

(deftype FusedAtom [f state]
  clojure.lang.IDeref
  (deref [this]
    (binding [*watcher* this]
      @@state))
  LazyWatchable
  (lazy-watch [this f]
    (add-watch state f (fn [_ _ _ _] (f)))))

(defn dirty-fused! [fused]
  (reset! (.state fused)
          (delay ((.f fused)))))

(defn make-fused-atom [f]
  (FusedAtom. f (atom (delay (f)))))

(defn deref-and-notify [ref & args]
  (let [watcher *watcher*]
    (lazy-watch ref #(dirty-fused! watcher)))
  (apply deref ref args))

(defn- replace-derefs [expr]
  (cond
    (seq? expr) (map replace-derefs expr)
    (vector? expr) (mapv replace-derefs expr)
    (map? expr) (into {} (map replace-derefs expr))
    (set? expr) `(hash-set ~@(map replace-derefs expr))
    (= `~expr `deref) `deref-and-notify
    :else expr))

(defmacro fuse [& body]
  `(make-fused-atom (fn [] ~@(map replace-derefs body))))
