(ns perepisun.handlers
  (:require
   [telegrambot-lib.core :as tbot]
   [clojure.string :as str]
   [perepisun.config :refer [config]]
   [taoensso.timbre :as log]
   [taoensso.carmine :as car]
   [perepisun.redis :refer [wcar*]]
   ))

(def mybot (tbot/create (:bot-api-key config))) ;; TODO: remove

(def help-msg
  "I can help you learn a new alphabet by substituting given letters in one alphabet with a similar sounding letter from another. For example, if you want to learn Georgian, you can set up a substitution of Latin letters 'd', 'a', 'o' with this command: `/set d დ a ა o ო`.

Use `/show` for getting current substitutions.")

(defn help [msg]
  (let [chat-id (-> msg :message :chat :id)]
    (when-not chat-id
      (log/debug "help: empty chat-id " msg))
    (tbot/send-message mybot chat-id help-msg #_send-msg-opts-)))

;; TODO: don't rewrite msg if it's the same

(defn todo [{{:keys [chat]} :message :as _event}]
  (tbot/send-message mybot (:id chat) "პოკა ნე უმეიუ"  #_send-msg-opts-)
  {:status 200 :body "all ok"})

(defn start [state]
  )

(defn stop [state]
  )

(defn status [state]
  )

(defn show [{msg :message :as _event}]
  (try
    (let [chat-id (-> msg :chat :id)
          m       (wcar* (car/get chat-id))]
      (tbot/send-message mybot chat-id (str "current mapping: " m) #_send-msg-opts-))
    (catch Exception e
      (log/error "error on add: " e)))  ;; rethrow ex-info, do logging only once
  {:status 200 :body "all ok"})

;; TODO: only admins should be able to change the mappings
;; TODO: handle bad input (odd number of elements)
(defn set [{msg :message :as _event}]
  (try
    (let [chat-id (-> msg :chat :id)
          tmp     (-> msg :text (str/split #"\s+") rest)
          m       (apply hash-map tmp)]
      (wcar* (car/set chat-id m))
      (tbot/send-message mybot chat-id (str "new mappings has been set: " m) #_send-msg-opts-))
    (catch Exception e
      (log/error "error on set: " e)))

  {:status 200 :body "all ok"}
  )
