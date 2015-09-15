(set-env!
  :resource-paths #{"src"}
  :source-paths #{"test"}
  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [org.clojure/clojurescript "1.7.48"]
                  [crisptrutski/boot-cljs-test "0.2.0-SNAPSHOT" :scope "test"]
                  [adzerk/boot-test "1.0.4" :scope "test"]])

(require '[adzerk.boot-test :refer [test]])
(require '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  pom {:project 'twinfoxcreations/fusion
       :version "0.0.0"}
  test {:namespaces '[fusion.test]})
