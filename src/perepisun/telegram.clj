(ns perepisun.telegram
  (:require
   [integrant.core :as ig]
   [telegrambot-lib.core :as tbot]))

(defmethod ig/init-key :app/tbot [_ {config :config}]
  (tbot/create (-> config :bot-api-key)))

(defn chat-id [msg] (-> msg :chat :id))
