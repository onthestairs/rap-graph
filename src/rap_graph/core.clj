(ns rap-graph.core
  (:require [rap-graph.graph :as g]
            [rap-graph.scrape :as scrape]
            [clojure.math.combinatorics :as combo]
            [clojure.core.async :refer [go chan timeout >! alts!! close!]]))

(defn add-scrape-to-graph [graph artists]
  (let [artist-combinations (combo/combinations artists 2)]
    (reduce (fn [graph [artist-1 artist-2]]
              (g/add-edge graph artist-1 artist-2))
            graph
            artist-combinations)))

(defn crawl [seed-artist depth ch]
  (def artists-seen (atom #{}))
  (letfn [(crawl-artist [artist depth ch]
            (println artist)
            (swap! artists-seen conj artist)
            (go (let [songs (scrape/top-songs artist)]
                  (doseq [song songs]
                    (crawl-song song depth ch)))))
          (crawl-song [url depth ch]
            (go (let [artists (scrape/find-artists url)]
                  (>! ch artists)
                  (doseq [artist artists]
                    (when (and (pos? depth) (not (contains? @artists-seen artist)))
                      (crawl-artist artist (dec depth) ch)))
                  )))]
    (crawl-artist seed-artist depth ch)))

(defn make-graph [seed-artist depth]
  (def g (atom {}))
  (let [ch (chan)]
    (crawl seed-artist depth ch)
    (loop []
      (let [tmo (timeout 10000)
            [msg chan] (alts!! [tmo ch])]
        (if (= chan tmo)
          (do (close! ch)
              (println "Finished"))
          (do
            (swap! g add-scrape-to-graph msg)
            ;(println g)
            (recur)))))
    @g))
