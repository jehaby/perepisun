(ns perepisun.perepisun
  (:require
   [integrant.core :as ig]
   [perepisun.system :refer [system]]))

(defn -main [& _args]
  ;; (taoensso.timbre.tools.logging/use-timbre)
  (ig/init system))
