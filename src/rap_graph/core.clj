(ns rap-graph.core
  (:require [loom.graph :as g]
            [loom.alg :as ga]
            [loom.attr :as graph-attr]
            [rap-graph.scrape :as scrape]
            [clojure.math.combinatorics :as combo]
            [clojure.core.async :refer [go chan timeout >! alts!! close!]]
            [clojure.string :as string]))

(defn add-scrape-to-graph [graph artists song]
  (let [canonical-artists (map :canonical-name artists)
        artist-combinations (combo/combinations canonical-artists 2)
        new-graph (reduce (fn [graph [artist-1 artist-2]]
                  (g/add-edges graph [artist-1 artist-2]))
                graph
                artist-combinations)]
    (println song)
    (graph-attr/add-attr-to-edges new-graph :song song artist-combinations)))

(defn crawl [seed-artist depth ch]
  (def artists-seen (atom #{}))
  (letfn [(crawl-artist [artist depth ch]
            (println artist)
            (swap! artists-seen conj artist)
            (let [songs (scrape/find-songs artist)]
                (doseq [song songs]
                  (crawl-song song depth ch))))
          (crawl-song [song depth ch]
            (let [artists (scrape/find-artists song)]
              (go (>! ch  {:artists artists :song song})
                  (doseq [artist artists]
                    (when (and (pos? depth) (not (contains? @artists-seen artist)))
                      (crawl-artist artist (dec depth) ch))))
                   ))]
    (crawl-artist seed-artist depth ch)))

(defn mock-crawl [ch]
  (dotimes [i 10]
    (println i)
    (go (>! ch {:artists [(str "a" i) (str "a" (* i 2)) (str "a" (* i 3))]
                :url (str "url" i)}))))

(defn make-graph [seed-artist depth]
  (def rg (atom (g/graph)))
  (let [ch (chan)]
    (crawl seed-artist depth ch)
    ;(mock-crawl ch)
    (loop []
        (let [tmo (timeout 10000)
              [msg chan] (alts!! [tmo ch])]
          (if (= chan tmo)
            (do (close! ch)
                (println "Finished"))
            (do
              (swap! rg add-scrape-to-graph  (:artists msg) (:song msg))
                                        ;(println g)
              (recur)))))
    @rg))

(defn save-graph [graph graph-file attrs-file]
  (spit graph-file (:adj graph))
  (spit attrs-file (:attrs graph)))

