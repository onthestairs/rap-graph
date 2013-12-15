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

(defn smallest-distance [distances nodes]
  (apply min-key
         (fn [node] (if (contains? distances node)
                     (distances node)
                     infinity))
         nodes))

(defn update-distances [distances distance nodes]
  (reduce (fn [distances node]
            (if (contains? distances node)
              distances
              (assoc distances node distance)))
          distances
          nodes))

(defn dijkstra [graph node-1]
  (loop [distances {node-1 0}
         unvisited (set (keys graph))]
    (if (empty? unvisited)
      distances
      (let [current-node (smallest-distance distances unvisited)]
        (if (not (contains? distances current-node))
          distances
          (let [current-distance (distances current-node)
                current-neighbours (get-node graph current-node)
                new-distances (update-distances distances (inc current-distance) current-neighbours)
                new-unvisited (disj unvisited current-node)]
            (recur new-distances
                   new-unvisited)))))))

(defn all-distances [graph]
  (let [nodes (keys graph)]
    (zipmap nodes
            (map (fn [node] (println "Doing" node) (dijkstra graph node))
                 nodes))))

(defn distance [distances node-1 node-2]
  ((distances node-1) node-2))
