(ns redis-repl
  (:require
   [taoensso.carmine :as car]
   [perepisun.redis :refer [wcar*]]))

(comment

  (wcar* (car/ping))

  (wcar* (car/set "foo" {"a" "foo"}))

  (wcar* (car/get -1001491248945))

  )
