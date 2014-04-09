(defproject rap-graph "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [enlive "1.1.5"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/math.combinatorics "0.0.7"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [aysylu/loom "0.4.2"]
                 [compojure "1.1.6"]
                 [ring/ring-json "0.3.0"]]
  :plugins [[lein-ring "0.8.7"]]
  :ring {:handler rap-graph.routes/app})


