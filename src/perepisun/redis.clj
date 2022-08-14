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
        (car/wcar conn-opts (car/parse-map (car/hgetall chat-id) :keywordize)))
      (set-mappings [_ chat-id mappings pattern]
        (car/wcar
          conn-opts
          (car/hset chat-id :mappings mappings :pattern pattern)))
      (stop [_ chat-id]
        (car/wcar conn-opts (car/hset chat-id :stopped? true)))
      (start [_ chat-id]
        (car/wcar conn-opts (car/hdel chat-id :stopped?))))))
