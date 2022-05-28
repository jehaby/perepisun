(ns user
  (:require
   [perepisun.http :refer [start-server]]))

(defonce srv (atom nil))
(comment
  (reset! srv (start-server nil))
  (.start @srv)
  (.stop @srv)
  )
