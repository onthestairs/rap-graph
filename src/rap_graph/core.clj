(ns rap-graph.core
  (:require ;[rap-graph.graph :as g]
            [loom.graph :as g]
            [loom.alg :as ga]
            [loom.attr :as graph-attr]
            [rap-graph.scrape :as scrape]
            [clojure.math.combinatorics :as combo]
            [clojure.core.async :refer [go chan timeout >! alts!! close!]]
            [clojure.data.json :as json]
            [clojure.string :as string]))

(defn add-scrape-to-graph [graph artists song]
  (let [artist-combinations (combo/combinations artists 2)
        new-graph (reduce (fn [graph [artist-1 artist-2]]
                  (g/add-edges graph [artist-1 artist-2]))
                graph
                artist-combinations)]
    (graph-attr/add-attr-to-edges new-graph :song song artist-combinations)))

(defn crawl [seed-artist depth ch]
  (def artists-seen (atom #{}))
  (letfn [(crawl-artist [artist depth ch]
            (println artist)
            (swap! artists-seen conj artist)
            (let [songs (scrape/find-songs artist)]
                (doseq [song songs]
                  (crawl-song song depth ch))))
          (crawl-song [url depth ch]
            (let [artists (scrape/find-artists url)]
              (go (>! ch  {:artists artists :url url})
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
              (print msg)
              (swap! rg add-scrape-to-graph (:artists msg) (:url msg))
                                        ;(println g)
              (recur)))))
    @rg))


(defn artist-path [graph start finish]
  (let [path (ga/bf-path graph start finish)]
    (map (fn [[artist1 artist2]]
           {:collab (str artist1 " -> " artist2)
            :song (:song (graph-attr/attrs graph artist1 artist2))})
         (partition 2 1 path))))

(defn artist-path-2 [graph start finish]
  (let [path (ga/bf-path graph (keyword start) (keyword finish))]
    (map (fn [[artist1 artist2]]
           {:collab (str artist1 " -> " artist2)})
         (partition 2 1 path))))

(defn get-image [artist]
  (let [artist (string/replace artist " " "%20")
        url (str "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&imgtype=face&imgsz=medium&q=" artist)
        text (slurp url)
        data (json/read-str text)
        image-url (get-in data ["responseData" "results" 0 "url"])]
    (.println System/out url)
    (str "<html><body><img src='" image-url "'></body></html>" )))

(def g2
  (g/add-edges (g/graph)
               [:austin :ian]
               [:tony :nigel]
               [:austin :nigel]))

(def g3
  (g/BasicEditableGraph. #{:austin :nigel :ian :tony}
                         {:nigel #{:austin :tony}, :tony #{:nigel}, :ian #{:austin}, :austin #{:nigel :ian}}))
