(ns perepisun.handlers.rewrite
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [taoensso.carmine :as car]
   [taoensso.timbre :as log]
   [telegrambot-lib.core :as tbot]
   [perepisun.redis :refer [wcar*]]
   ))

(defn respond [text mappings #_regexp]
  (if (seq mappings)
    (let [regexp   (re-pattern (->> mappings keys (str/join "|"))) ;; TODO: cache in redis
          new-text (str/replace text regexp mappings)]
      new-text)
    text))

(defn- add-author [from text]
  (let [{:keys [first_name last_name]} from]
    (str "`" (str first_name " " last_name) "` => " text)))

(defmethod ig/init-key :handler/rewrite [_ {tbot :tbot}]
  (fn rewrite-handler [{{:keys [chat message_id from text] :as message} :message :as event}]
    (def tbot tbot)
    (def event event)
    (def chat chat)
    (def message_id message_id)
    (def from from) (def text text)
    (when (and text (:id chat) message_id from)
      (try
        (let [chat-id  (:id chat)
              mappings (wcar* (car/get (:id chat)))
              new-text (respond text mappings)]
          ;; (def mappings mappings)
          #_(log/debug "got message" {:chat-id chat-id :msg-id message_id})
          (when (not= new-text text)
            (tbot/delete-message tbot chat-id message_id) ;; TODO: check and handler response
            (let [msg  (add-author from new-text)
                  resp (tbot/send-message tbot chat-id msg  #_send-msg-opts-)]
              (log/debug "got resp " resp)
              )))
        (catch Exception e
          (throw (ex-info "Excpetion in rewrite-handler" {:message message} e)))))))

