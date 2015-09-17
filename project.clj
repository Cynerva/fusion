(defproject twinfoxcreations/fusion "0.1.0"
  :description "State propagation via atoms."
  :license {:name "CC0 1.0 Universal"
            :url "https://creativecommons.org/publicdomain/zero/1.0/"}
  :url "https://github.com/Cynerva/fusion"
  :plugins [[lein-cljsbuild "1.1.0"]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]]
  :cljsbuild {:builds [{:source-paths ["src" "test"]
                        :compiler {:output-to "target/fusion-tests.js"
                                   :target :nodejs
                                   :optimizations :simple}}]
              :test-commands {"node" ["node" "target/fusion-tests.js"]}})
