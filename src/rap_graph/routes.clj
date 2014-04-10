(ns rap-graph.routes
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [ring.middleware.json :as middleware]
            [rap-graph.views :refer :all]))

(defroutes main-routes
  (GET "/path" [from-artist to-artist] (artist-path from-artist to-artist))
  (GET "/image" [artist] (get-image artist))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (middleware/wrap-json-response)))
