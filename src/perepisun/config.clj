(ns perepisun.config
  (:require
   [clojure.java.io :as io]
   [aero.core :as aero]
   [integrant.core :as ig]))

(defn read-config []
  (aero/read-config  (io/resource "config.edn")))

(defmethod ig/init-key :app/config [_ _]
  (read-config))
