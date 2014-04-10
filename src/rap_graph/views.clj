(ns rap-graph.views
  (:require
   [clojure.string :as string]
   [clojure.data.json :as json]
   [loom.graph :as g]
   [rap-graph.graph :as graph]
   [ring.util.response :refer [resource-response response]]))


(defn get-image [artist]
  (let [artist (string/replace artist " " "%20")
        url (str "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&imgtype=face&imgsz=medium&q=" artist)
        text (slurp url)
        data (json/read-str text)
        image-url (get-in data ["responseData" "results" 0 "url"])]
    image-url))

(defn add-images [path]
  (map (fn [step] (assoc step :from-artist-image (get-image (:from-artist step))
                              :to-artist-image (get-image (:to-artist step))))
       path))

(defn artist-path [from-artist to-artist]
  (response (add-images (graph/artist-path graph/graph from-artist to-artist))))
