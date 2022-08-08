(ns perepisun.config
  (:require
   [clojure.java.io]
   [aero.core :as aero]
   [integrant.core :as ig]))

(defn read-config []
  (aero/read-config  (clojure.java.io/resource "config.edn")))

(defmethod ig/init-key :app/config [_ _]
  (read-config))
