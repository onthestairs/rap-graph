(ns rap-graph.core
  (:require [rap-graph.graph :as g]
            [rap-graph.scrape :as scrape]
            [clojure.math.combinatorics :as combo]))

(defn create-add-edge-fn [artist-1 artist-2]
  (fn [graph] (g/add-edge graph artist-1 artist-2)))

(defn add-scrape-to-graph [graph artists]
  (let [add-edge-fns (map (fn [[artist-1 artist-2]] (create-add-edge-fn artist-1 artist-2))
                          (combo/combinations artists 2))
        add-edges (apply comp add-edge-fns)]
    (add-edges graph)))

(defn create-add-scrape-fn [artists]
  (println "Adding artists" artists)
  (fn [graph]
    (println "Calling add-scrape to graph with " graph artists)
    (add-scrape-to-graph graph artists)))

(defn add-scrapes-to-graph [graph urls-to-scrape]
  (println urls-to-scrape)
  (let [add-scrape-fns (map (fn [url-to-scrape] (create-add-scrape-fn (scrape/find-artists url-to-scrape)))
                            urls-to-scrape)
        add-scrapes (apply comp add-scrape-fns)]
    (add-scrapes graph)))

(def urls-to-scrape
  ["http://rapgenius.com/Big-sean-control-lyrics"
   "http://rapgenius.com/A-ap-rocky-fuckin-problems-lyrics"
   "http://rapgenius.com/Drake-all-me-lyrics"
   "http://rapgenius.com/Dr-dre-forgot-about-dre-lyrics"
   "http://rapgenius.com/Dr-dre-still-dre-lyrics"])