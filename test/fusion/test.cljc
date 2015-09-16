(ns fusion.test
  #?(:clj (:require [clojure.test :refer [deftest is]]
                    [fusion.core :refer [fuse]])
     :cljs (:require [cljs.test :refer-macros [deftest is]]
                     [fusion.core :refer-macros [fuse]])))

(deftest fuse-constant
  "Can fuse a constant"
  (is (= @(fuse :value) :value)))

(deftest fuse-changes
  "Fused atom changes when the original does"
  (let [a (atom :value)
        f (fuse @a)]
    (is (= @f :value))
    (reset! a :changed)
    (is (= @f :changed))))

(deftest fuse-function-call
  "Derefs within function calls are watched properly"
  (let [a (atom 0)
        f (fuse (inc @a))]
    (is (= @f 1))
    (reset! a 1)
    (is (= @f 2))))

(deftest fuse-vector
  "Derefs within vectors are watched properly"
  (let [a (atom :value)
        f (fuse [@a])]
    (is (= @f [:value]))
    (reset! a :changed)
    (is (= @f [:changed]))))

(deftest fuse-map
  "Derefs within maps are watched properly"
  (let [a (atom :value)
        f (fuse {:a @a})]
    (is (= @f {:a :value}))
    (reset! a :changed)
    (is (= @f {:a :changed}))))

(deftest fuse-set
  "Derefs within sets are watched properly"
  (let [a (atom :value)
        f (fuse #{@a})]
    (is (= @f #{:value}))
    (reset! a :changed)
    (is (= @f #{:changed}))))

(deftest fuse-set-conflict
  "Derefs within sets can handle conflicts"
  (let [a (atom :value)
        b (atom :value)
        f (fuse #{@a @b})]
    (is (= @f #{:value}))))

(deftest fuse-nested-derefs
  "Can fuse derefs within derefs. Don't know why you would do this but hey."
  (let [a (atom :value)
        b (atom a)
        f (fuse @@b)]
    (is (= @f :value))
    (reset! a :changed)
    (is (= @f :changed))
    (reset! b (atom :new-atom))
    (is (= @f :new-atom))))

(deftest fuse-is-lazy
  "Fused atom doesn't evaluate until it is deref'd"
  (let [evaluated (atom false)
        a (atom :value)
        f (fuse (reset! evaluated true) @a)]
    (is (= @evaluated false))
    @f
    (is (= @evaluated true))))

(deftest fuse-only-evals-once
  "Fused atom only evaluates once (until a change is observed)"
  (let [eval-count (atom 0)
        a (atom :value)
        f (fuse (swap! eval-count inc)
                @a)]
    @f
    @f
    (is (= @eval-count 1))))

(deftest fuse-fused
  "Can fuse another fused atom"
  (let [a (atom :value)
        f1 (fuse @a)
        f2 (fuse @f1)]
    (is (= @f2 :value))
    (reset! a :changed)
    (is (= @f2 :changed))))

(deftest fuse-delay
  "Can safely fuse a deref within a delay"
  (let [a (atom :value)
        f (fuse (delay @a))]
    (is (= @@f :value))
    (reset! a :changed)
    (is (= @@f :changed))))
