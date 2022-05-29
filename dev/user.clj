(ns user
  (:require
   [integrant.core :as ig]
   [perepisun.system :refer [system]]))

(defonce dev-sys (atom nil))
(comment
  (reset! dev-sys (ig/init system))
  (.start @dev-sys)
  (.stop @dev-sys)
  )
