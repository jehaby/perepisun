(ns perepisun.handlers
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [taoensso.timbre :as log]
   [taoensso.carmine :as car]
   [telegrambot-lib.core :as tbot]
   [perepisun.redis :refer [wcar*] :as redis]))

(def mybot nil)

(def help-msg
  "I can help you learn a new alphabet by substituting given letters in one alphabet with a similar sounding letter from another. For example, if you want to learn Georgian, you can set up a substitution of Latin letters 'd', 'a', 'o' with this command: `/set d დ a ა o ო`.

Use `/show` for getting current substitutions.")

(defmethod ig/init-key :handler/help [_ {:keys [tbot tg-send-message]}]
  (fn help [msg]
    (let [chat-id (-> msg :message :chat :id)]
      (when-not chat-id
        (log/debug "help: empty chat-id " msg))
      (tg-send-message tbot chat-id help-msg #_send-msg-opts-))))

(defn todo [{{:keys [chat]} :message  :as _event}]
  (tbot/send-message mybot (:id chat) "პოკა ნე უმეიუ"  #_send-msg-opts-)
  {:status 200 :body "all ok"})

(defn start [state])

(defn stop [state])

(defn status [state])

(defmethod ig/init-key :handler/show [_ {:keys [tbot db tg-send-message]}]
  (fn show [{msg :message :as _event}]
    (log/debug "in show handler  " _event)
    (try
      (let [chat-id              (-> msg :chat :id)
            {mappings :mappings} (redis/get-mappings db chat-id)]
        (tg-send-message tbot chat-id (str "current mapping: " mappings)))
      (catch Exception e
        (log/error "error on add: " e)))  ;; TODO: rethrow ex-info, do logging only once
    {:status 200 :body "all ok"}))

;; TODO: only admins should be able to change the mappings
(defmethod ig/init-key :handler/set [_ {:keys [tbot tg-send-message]}]
  (fn set [{msg :message :as _event}]
    (let [chat-id  (-> msg :chat :id)
          tmp      (-> msg :text (str/split #"\s+") rest)]
      (try
        (let [mappings (apply hash-map tmp)
              pattern  (->> tmp (take-nth 2) (str/join "|"))]
          (wcar* (car/set chat-id {:mappings mappings :pattern pattern}))
          (tg-send-message tbot chat-id (str "New mappings has been set: " mappings)))
        (catch java.lang.IllegalArgumentException e
          (let [msg (ex-message e)]
            (if (str/starts-with? msg "No value supplied for key")
              (tg-send-message tbot chat-id (format "Mappings must have even number of elements (%s)" msg))
              (throw e))))
        (catch Exception e
          (log/error "error on set: " e))))
    {:status 200 :body "all ok"}))
