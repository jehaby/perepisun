(ns user
  (:require
   [perepisun.system :refer [system]]
   [taoensso.timbre :as log]
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   [integrant.repl.state :as igrs]))

(integrant.repl/set-prep! (constantly system))

(def db (-> igrs/system :app/redis))
(def config (-> igrs/system :app/config))

(log/merge-config! (:log config))

(def help-hnd (-> igrs/system :handler/help))

(comment
  igrs/config
  igrs/system

  (go) ; prep & init
  (reset))
