(ns perepisun.handlers.rewrite
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [taoensso.timbre :as log]
   [telegrambot-lib.core :as tbot]
   [perepisun.redis :as db]))

(defn respond [text mappings pattern]
  (if (seq mappings)
    (let [regexp   (re-pattern pattern) ;; TODO: cache in redis
          new-text (str/replace text regexp mappings)]
      new-text)
    text))

(defn- add-author [from text]
  (let [{:keys [first_name last_name]} from]
    (str "`" (str first_name " " last_name) "` => " text)))

(defmethod ig/init-key :handler/rewrite [_ {:keys [tbot db]}]
  (fn rewrite-handler [{{:keys [chat message_id from text] :as message} :message :as event}]
    (when (and text (:id chat) message_id from)
      (try
        (let [chat-id  (:id chat)
              {:keys [mappings pattern]} (db/get-mappings db (:id chat))
              new-text (respond text mappings pattern)]
          (when (not= new-text text)
            (tbot/delete-message tbot chat-id message_id) ;; TODO: check and handler response
            (let [msg  (add-author from new-text)
                  resp (tbot/send-message tbot chat-id msg  #_send-msg-opts-)]
              (log/debug "telegram api resp: " resp))))
        (catch Exception e
          (throw (ex-info "Excpetion in rewrite-handler" {:message message} e)))))))
