(ns perepisun.handlers
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [telegrambot-lib.core :as tbot]
   [taoensso.timbre :as log]
   [taoensso.carmine :as car]
   [perepisun.redis :refer [wcar*]]))

(def mybot nil)

(def help-msg
  "I can help you learn a new alphabet by substituting given letters in one alphabet with a similar sounding letter from another. For example, if you want to learn Georgian, you can set up a substitution of Latin letters 'd', 'a', 'o' with this command: `/set d დ a ა o ო`.

Use `/show` for getting current substitutions.")

(defmethod ig/init-key :handler/help [_ {:keys [tbot]}]
  (fn help [msg]
    (let [chat-id (-> msg :message :chat :id)]
      (when-not chat-id
        (log/debug "help: empty chat-id " msg))
      (tbot/send-message tbot chat-id help-msg #_send-msg-opts-))))

(defn todo [{{:keys [chat]} :message :as _event}]
  (tbot/send-message mybot (:id chat) "პოკა ნე უმეიუ"  #_send-msg-opts-)
  {:status 200 :body "all ok"})

(defn start [state])

(defn stop [state])

(defn status [state])

(defmethod ig/init-key :handler/show [_ {:keys [tbot]}]
  (fn show [{msg :message :as _event}]
    (log/debug "in show handler  " _event)
    (try
      (let [chat-id              (-> msg :chat :id)
            {mappings :mappings} (wcar* (car/get chat-id))]
        (tbot/send-message tbot chat-id (str "current mapping: " mappings) #_send-msg-opts-))
      (catch Exception e
        (log/error "error on add: " e)))  ;; rethrow ex-info, do logging only once
    {:status 200 :body "all ok"}))

;; TODO: only admins should be able to change the mappings
;; TODO: handle bad input (odd number of elements)
(defmethod ig/init-key :handler/set [_ {:keys [tbot]}]
  (fn set [{msg :message :as _event}]
    (try
      (let [chat-id  (-> msg :chat :id)
            tmp      (-> msg :text (str/split #"\s+") rest)
            pattern  (->> tmp (take-nth 2) (str/join "|"))
            mappings (apply hash-map tmp)]
        (wcar* (car/set chat-id {:mappings mappings :pattern pattern}))
        (tbot/send-message mybot chat-id (str "new mappings has been set: " mappings) #_send-msg-opts-))
      (catch Exception e
        (log/error "error on set: " e)))
    {:status 200 :body "all ok"}))
