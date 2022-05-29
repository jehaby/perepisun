(ns user
  (:require
   [perepisun.system :refer [system]]
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   [integrant.repl.state :as igrs]
   ))

(integrant.repl/set-prep! (constantly system))

(comment
  igrs/config
  igrs/system

  (go) ; prep & init
  (reset))
