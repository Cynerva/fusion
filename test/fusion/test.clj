(ns fusion.test
  (:require [clojure.test :refer [deftest is]]
            [fusion.core :refer [fuse]]))

(deftest fuse-constant
  "Can fuse a constant"
  (is (= @(fuse :value) :value)))

(deftest fuse-simple
  "Fused atom holds the same value as the original"
  (let [a (atom :value)
        f (fuse @a)]
    (is (= @f :value))))

(deftest fuse-changes
  "Fused atom changes when the original does"
  (let [a (atom :value)
        f (fuse @a)]
    (reset! a :changed)
    (is (= @f :changed))))

(deftest fuse-function-call
  "Derefs within function calls are watched properly"
  (let [a (atom 0)
        f (fuse (inc @a))]
    (reset! a 1)
    (is (= @f 2))))

(deftest fuse-vector
  "Derefs within vectors are watched properly"
  (let [a (atom :value)
        f (fuse [@a])]
    (reset! a :changed)
    (is (= @f [:changed]))))
