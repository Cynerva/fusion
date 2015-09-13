(ns fusion.test
  (:require [clojure.test :refer [deftest is]]
            [fusion.core :refer [fuse]]))

(deftest fuse-simple
  "Fused atom holds the same value as the original"
  (let [a (atom :value)
        f (fuse @a)]
    (is (= @f :value))))
