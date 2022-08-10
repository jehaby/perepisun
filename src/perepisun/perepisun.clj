(ns perepisun.perepisun
  (:gen-class)
  (:require
   [integrant.core :as ig]
   [taoensso.timbre :as log]
   [taoensso.timbre.tools.logging]
   [perepisun.system :refer [system]]))

(defn -main [& _args]
  (taoensso.timbre.tools.logging/use-timbre)
  (let [{config :app/config} (ig/init system)]
    (log/set-level! (:log-level config))))
