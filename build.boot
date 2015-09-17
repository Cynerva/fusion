(def dependencies '[[org.clojure/clojure "1.7.0"]
                    [org.clojure/clojurescript "1.7.48"]])

(def dev-dependencies (into dependencies
                        '[[adzerk/bootlaces "0.1.12"]
                          [adzerk/boot-test "1.0.4"]
                          [crisptrutski/boot-cljs-test "0.2.0-SNAPSHOT"]]))

(set-env!
  :resource-paths #{"src"}
  :source-paths #{"test"}
  :dependencies dev-dependencies)

(require '[adzerk.bootlaces :refer [push-snapshot]])
(require '[adzerk.boot-test :refer [test]])
(require '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  pom {:project 'twinfoxcreations/fusion
       :version "0.1.0-SNAPSHOT"}
  test {:namespaces '[fusion.test]}
  push {:repo "clojars"})

(deftask build
  "Build the jar!"
  []
  (set-env! :dependencies dependencies)
  (comp (pom) (jar)))
