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
