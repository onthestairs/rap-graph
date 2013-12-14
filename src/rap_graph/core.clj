(ns rap-graph.core
  (:require [rap-graph.graph :as g]
            [rap-graph.scrape :as scrape]
            [clojure.math.combinatorics :as combo]
            [clojure.core.async :refer [go chan timeout >! alts!!]]))

(defn create-add-edge-fn [artist-1 artist-2]
  (fn [graph] (g/add-edge graph artist-1 artist-2)))

(defn add-scrape-to-graph [graph artists]
  (let [add-edge-fns (map (fn [[artist-1 artist-2]] (create-add-edge-fn artist-1 artist-2))
                          (combo/combinations artists 2))
        add-edges (apply comp add-edge-fns)]
    (add-edges graph)))

(def urls-to-scrape
  ["http://rapgenius.com/Big-sean-control-lyrics"
   "http://rapgenius.com/A-ap-rocky-fuckin-problems-lyrics"
   "http://rapgenius.com/Drake-all-me-lyrics"
   "http://rapgenius.com/Dr-dre-forgot-about-dre-lyrics"
   "http://rapgenius.com/Dr-dre-still-dre-lyrics"])

(def artists-seen (atom #{}))

(defn crawl-song [url depth ch]
  (go (let [artists (scrape/find-artists url)]
        (>! ch artists)
        (println url "yielded" artists)
        (doseq [artist artists]
          (println "Looking at" artist)
          (when (and (pos? depth) (not (contains? @artists-seen artist)))
            (crawl-artist artist (dec depth) ch))
          ; (crawl-artist artist 4 ch)
          ;(if-not (contains? artists-seen artist) (crawl-artist artist 4 ch))
          )
        )))

(defn crawl-artist [artist depth ch]
  (println "Crawling" artist)
  (swap! artists-seen conj artist)
  (go (let [songs (take 3 (scrape/top-songs artist))]
        (doseq [song songs]
          (crawl-song song depth ch)))))

(def g (atom {}))

(defn crawl [artist depth ch]
  (let [crawler (fn crawler [artist depth]
                  (go
                   (when (and (pos? depth) (not (@artists-seen artist)))
                     (swap! artists-seen conj artist)
                     (crawl-artist artist ch))))]))

(defn make-graph [seed-artist]
  (let [ch (chan)]
    (crawl-artist seed-artist 4 ch)
    (loop []
      (let [tmo (timeout 10000)
            [msg chan] (alts!! [tmo ch])]
        (if (= chan tmo)
          (println "done")
          (do
            (swap! g add-scrape-to-graph msg)
            (println g)
            (recur)))))))
