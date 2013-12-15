(ns rap-graph.graph)

(defn get-node [graph node-name]
  (if (contains? graph node-name)
    (graph node-name)
    #{}))

(defn add-node-edge [node node-name]
  (conj node node-name))

(defn add-edge [graph node-1-name node-2-name]
  (let [node-1 (get-node graph node-1-name)
        new-node-1 (add-node-edge node-1 node-2-name)
        node-2 (get-node graph node-2-name)
        new-node-2 (add-node-edge node-2 node-1-name)]
    (assoc graph node-1-name new-node-1 node-2-name new-node-2)))

(def infinity (java.lang.Integer/MAX_VALUE))

(defn smallest-length [distances nodes]
  (apply min-key
         (fn [node] (if (contains? distances node)
                     ((distances node) :length)
                     infinity))
         nodes))

(defn update-paths [paths path nodes]
  (let [route (path :route)
        length (inc (path :length))]
    (reduce (fn [paths node]
              (if (contains? paths node)
                paths
                (assoc paths node {:length length
                                   :route (conj route node)})))
            paths
            nodes)))

(defn dijkstra [graph node-1]
  (loop [paths {node-1 {:length 0
                        :route [node-1]}}
         unvisited (set (keys graph))]
    (if (empty? unvisited)
      paths
      (let [current-node (smallest-length paths unvisited)]
        (if (not (contains? paths current-node))
          paths
          (let [current-path (paths current-node)
                current-neighbours (get-node graph current-node)
                new-paths (update-paths paths current-path current-neighbours)
                new-unvisited (disj unvisited current-node)]
            (recur new-paths
                   new-unvisited)))))))

(defn all-distances [graph]
  (let [nodes (keys graph)]
    (zipmap nodes
            (map (fn [node] (println "Doing" node) (dijkstra graph node))
                 nodes))))

(defn distance [distances node-1 node-2]
  ((distances node-1) node-2))
