(set-env!
  :source-paths #{"src" "test"}
  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [adzerk/boot-test "1.0.4" :scope "test"]])

(require '[adzerk.boot-test :refer :all])
