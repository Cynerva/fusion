(ns fusion.core)

(defn watch [ref f]
  (add-watch ref f (fn [_ _ _ _] (f))))

(defn replace-derefs [watch-fn-sym expr]
  (cond
    (seq? expr) (if (= `~(first expr) `deref)
                  `(let [ref# ~(replace-derefs watch-fn-sym (second expr))]
                     (watch ref# ~watch-fn-sym)
                     (deref ref# ~@(map (partial replace-derefs watch-fn-sym)
                                        (drop 2 expr))))
                    (map (partial replace-derefs watch-fn-sym) expr))
    (vector? expr) (mapv (partial replace-derefs watch-fn-sym) expr)
    (map? expr) (into {} (map (partial replace-derefs watch-fn-sym) expr))
    :else expr))

(defmacro fuse [& body]
  (let [watch-fn-sym (gensym "watch-fn")]
    `(let [fused# (atom nil)]
       ((fn ~watch-fn-sym []
          (reset! fused# (do ~@(map (partial replace-derefs watch-fn-sym)
                                    body)))))
       fused#)))
