(ns fusion.core
  #?(:clj (:import [clojure.lang Atom IDeref])))

(def ^:dynamic *watcher*)

(defprotocol LazyWatchable
  (lazy-watch [this key f]))

(extend-type Atom
  LazyWatchable
  (lazy-watch [this key f]
    (add-watch this key (fn [_ _ _ _] (f)))))

(deftype FusedAtom [f state]
  IDeref
  (#?(:clj deref :cljs -deref) [this]
    (binding [*watcher* this]
      @@state))
  LazyWatchable
  (lazy-watch [this key f]
    (add-watch state key (fn [_ _ _ _] (f)))))

(defn dirty-fused! [fused]
  (reset! (.-state fused)
          (delay ((.-f fused)))))

(defn make-fused-atom [f]
  (FusedAtom. f (atom (delay (f)))))

(defn deref-and-notify [ref & args]
  (let [watcher *watcher*]
    (lazy-watch ref watcher #(dirty-fused! watcher)))
  (apply deref ref args))

#?(:clj
(defn- replace-derefs [expr]
  (cond
    (= `~expr `deref) `deref-and-notify
    (seq? expr) (map replace-derefs expr)
    (vector? expr) (mapv replace-derefs expr)
    (map? expr) (into {} (map replace-derefs expr))
    (set? expr) `(hash-set ~@(map replace-derefs expr))
    :else expr)))

#?(:clj
(defmacro fuse [& body]
  `(make-fused-atom (fn [] ~@(map replace-derefs body)))))
