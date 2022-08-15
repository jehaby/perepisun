(ns perepisun.handlers
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [perepisun.redis :as redis]
   [perepisun.telegram :as tg]
   [taoensso.timbre :as log]
   [telegrambot-lib.core :as tbot]))

(def mybot nil)

(def help-msg
  "I can help you learn a new alphabet by substituting given letters in one alphabet with a similar sounding letter from another. For example, if you want to learn Georgian, you can set up a substitution of Latin letters 'd', 'a', 'o' with this command: `/set d დ a ა o ო`.

Use `/show` for getting current substitutions.")

(defmethod ig/init-key :handler/help [_ {:keys [tbot tg-send-message]}]
  (fn help [{msg :message :as _event}]
    (let [chat-id (tg/chat-id msg)]
      (when-not chat-id
        (log/debug "help: empty chat-id " msg))
      (tg-send-message tbot chat-id help-msg))))

(defn todo [{{:keys [chat]} :message  :as _event}]
  (tbot/send-message mybot (:id chat) "პოკა ნე უმეიუ")
  {:status 200 :body "all ok"})

(defn status [{{:keys [chat]} :message  :as _event}])

(defmethod ig/init-key :handler/show [_ {:keys [tbot db tg-send-message]}]
  (fn show [{msg :message :as _event}]
    ;; (def db db)
    ;; (def tbot tbot)
    ;; (def msg msg)
    (log/debug "in show handler  " _event)
    (try
      (let [chat-id              (tg/chat-id msg)
            {mappings :mappings} (redis/get-mappings db chat-id)]
        (tg-send-message tbot chat-id (str "current mapping: " mappings)))
      (catch Exception e
        (log/error "error on add: " e)))  ;; TODO: rethrow ex-info, do logging only once
    {:status 200 :body "all ok"}))

;; TODO: only admins should be able to change the mappings
(defmethod ig/init-key :handler/set [_ {:keys [tbot db tg-send-message]}]
  (fn set [{msg :message :as _event}]
    (let [chat-id  (tg/chat-id msg)
          mappings-seq      (-> msg :text (str/split #"\s+") rest)]
      (try
        (let [mappings (apply hash-map mappings-seq)
              pattern  (->> mappings-seq (take-nth 2) (str/join "|"))]
          (redis/set-mappings db chat-id mappings pattern)
          (tg-send-message tbot chat-id (str "New mappings has been set: " mappings)))
        (catch java.lang.IllegalArgumentException e
          (let [msg (ex-message e)]
            (if (str/starts-with? msg "No value supplied for key")
              (tg-send-message tbot chat-id (format "Mappings must have even number of elements (%s)" msg))
              (throw e))))
        (catch Exception e
          (log/error "error on set: " e))))
    {:status 200 :body "all ok"}))

(defmethod ig/init-key :handler/start [_ {:keys [tbot db tg-send-message]}]
  (fn start [{msg :message :as _event}]
    (log/debug "in start handler  " _event)
    (try
      (let [chat-id (tg/chat-id msg)
            {mappings :mappings} (redis/get-mappings db chat-id)]
        (redis/start db chat-id)
        (let [text (if (seq mappings)
                     (format "Started. Current mappings: %s" mappings)
                     "You need to set some mappings (use /set command). For example `/set а ა у უ и ი`")]
          (tg-send-message tbot chat-id text)))
      (catch Exception e
        (log/error "error on add: " e)))  ;; TODO: rethrow ex-info, do logging only once
    {:status 200 :body "all ok"}))

(defmethod ig/init-key :handler/stop [_ {:keys [tbot db tg-send-message]}]
  (fn stop [{msg :message :as _event}]
    (log/debug "in stop handler  " _event)
    (try
      (let [chat-id (tg/chat-id msg)]
        (redis/stop db chat-id)
        (tg-send-message tbot chat-id "Stopped."))
      (catch Exception e
        (log/error "error on add: " e)))
    {:status 200 :body "all ok"}))

(defmethod ig/init-key :handler/status [_ {:keys [tbot db tg-send-message]}]
  (fn status [{msg :message :as _event}]
    (log/debug "in status handler  " _event)
    (try
      (let [chat-id (tg/chat-id msg)
            {:keys [mappings stopped?]} (redis/get-mappings db chat-id)
            text (format "Bot is%s running. \nCurrent mappings: %s. "
                         (if stopped? " not" "")
                         mappings)]
        (tg-send-message tbot chat-id text))
      (catch Exception e
        (log/error "error in status: " e)))
    {:status 200 :body "all ok"}))

(def about-msg
"For any questions, suggestions or bug-reports contact @jehaby.
source: https://github.com/jehaby/perepisun")

(defmethod ig/init-key :handler/about [_ {:keys [tbot tg-send-message]}]
  (fn about [{msg :message :as _event}]
    (let [chat-id (tg/chat-id msg)]
      (when-not chat-id
        (log/debug "about: empty chat-id " msg))
      (tg-send-message tbot chat-id about-msg))))
