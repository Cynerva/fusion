(defproject twinfoxcreations/fusion "0.1.0-SNAPSHOT"
  :plugins [[lein-cljsbuild "1.1.0"]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]]
  :cljsbuild {:builds [{:source-paths ["src" "test"]
                        :compiler {:output-to "target/fusion-tests.js"
                                   :target :nodejs
                                   :optimizations :simple}}]
              :test-commands {"node" ["node" "target/fusion-tests.js"]}})
