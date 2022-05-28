(ns perepisun.redis
  (:require
   [taoensso.carmine :as car :refer (wcar)]
   #_[jehaby.config :refer [config]]
   ))

(def -uri
  (str "redis://"
       ;; (-> config :redis :password)
       "localhost:6379/"
       ))

(def server1-conn
  {:pool {}
   :spec {:uri -uri}}) ; See `wcar` docstring for opts
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))


