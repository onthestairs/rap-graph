(ns rap-graph.graph
  (:require [loom.graph :as g]
            [loom.alg :as ga]
            [loom.attr :as graph-attr]))


(defn read-edn [file]
  (read-string (slurp file)))

(defn load-graph [file]
  (g/graph (read-edn file)))

(defonce graph
  (let [nodes-and-edges (load-graph "am.edn")
        attrs (read-edn "am.attrs.edn")]
    (assoc nodes-and-edges :attrs attrs)))

(defn artist-path [graph start finish]
  (let [path (ga/bf-path graph start finish)] ;diff alg?!
    (map (fn [[from-artist to-artist]]
           {:from-artist from-artist
            :to-artist to-artist
            :song (:song (graph-attr/attrs graph from-artist to-artist))})
         (partition 2 1 path))))
