(ns redis-repl
  (:require
   [taoensso.carmine :as car]
   [perepisun.redis :as db]
   [user :refer [db config]]
   ))

(defmacro wcar* [& body] `(car/wcar (:redis config) ~@body))

(comment

  (car/wcar (:redis config) (car/get "foo"))

  (wcar* (car/set "foo" {"a" "foo"}))

  (wcar* (car/get "foo"))

  (db/get-mappings db "foo")

  (wcar* (car/get -1001491248945))
  )
