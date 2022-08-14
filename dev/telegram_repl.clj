(ns telegram-repl
  (:require
   [telegrambot-lib.core :as tbot]
   [integrant.repl.state :refer [system]]))

(def tbot (:app/tbot system))
(def chat-id -1001491248945)

(defn btn [text]
  {:text text})

(comment
  (tbot/send-message tbot chat-id "hola"
                     {:reply_markup
                      {:keyboard
                       [[(btn "ა") (btn "ბ")]]}})

  )
