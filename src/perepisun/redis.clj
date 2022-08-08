(ns perepisun.redis
  (:require
   [integrant.core :as ig]
   [taoensso.carmine :as car]))

(defmacro wcar* [& body] `(car/wcar nil ~@body)) 

(defprotocol DB
  (get-mappings [this chat-id])
  (set-mappings [this chat-id mappings pattern])
  (stop [this chat-id])
  (start [this chat-id]))

(defmethod ig/init-key :app/redis [_ {config :config}]
  (let [conn-opts (:redis config)]
    (reify DB
      (get-mappings [_ chat-id]
        (car/wcar conn-opts (car/get chat-id)))
      (set-mappings [_ chat-id mappings pattern]
        (car/wcar
          conn-opts
          (car/set conn-opts chat-id {:mappings mappings :pattern pattern})))
      (stop [_ chat-id])
      (start [_ chat-id]))))
