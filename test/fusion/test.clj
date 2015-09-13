(ns fusion.test
  (:require [clojure.test :refer [deftest is]]
            [fusion.core :refer [fuse]]))

(deftest fused-atom-holds-same-value
  (let [a (atom :value)
        f (fuse @a)]
    (is (= @f :value))))
