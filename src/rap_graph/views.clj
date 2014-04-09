(ns rap-graph.views
  (:require
   [loom.graph :as g]
   [rap-graph.core :as core]
   [ring.util.response :refer [resource-response response]]))

(defn artist-path [from-artist to-artist]
  (.println System/out (g/nodes core/g2))
  (.println System/out (:adj core/g2))
  (response core/g3))
