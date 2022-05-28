(ns perepisun.config
  (:require
   [clojure.java.io]
   [aero.core :as aero]))

(def config
  (aero/read-config  (clojure.java.io/resource "config.edn")))
