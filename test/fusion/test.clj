(ns fusion.test
  (:require [clojure.test :refer [deftest is]]
            [fusion.core :refer [fuse]]))

(deftest fused-atom-holds-same-value
  (is (= @(fuse (atom :value))
         :value)))
