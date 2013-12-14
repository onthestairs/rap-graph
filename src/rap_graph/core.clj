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
  (fn [graph]
    (add-scrape-to-graph graph artists)))

(defn add-scrapes-to-graph [graph urls-to-scrape]
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

(def artists-seen (atom #{}))

(defn crawl-song [url c]
  (go (let [artists (scrape/find-artists url)]
        (doseq [artist artists]
          (if-not (contains? artists-seen artist)
            (crawl-artist artist c)))
        (>! c artists))))

(defn crawl-artist [artist ch]
  (swap! artists-seen conj artist)
  (let [songs (take 3 (scrape/top-songs artist))]
    (doseq [song songs]
      (go (crawl-song song ch)))))

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
      (let [tmo (timeout 5000)
            [msg chan] (alts!! [tmo ch])]
        (if (= chan tmo)
          (println "done")
          (do
            (swap! g add-scrape-to-graph msg)
            (println g)
            (recur)))))))
