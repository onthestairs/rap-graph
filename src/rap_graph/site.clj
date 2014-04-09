(ns rap-graph.site
  (:require [ring.middleware.json :refer :all]
            [ring.util.response :refer :all]))

(defn handler [request]
  (println "YO")
  (response {:foo "bar"}))

(def app
  (wrap-json-response handler))
