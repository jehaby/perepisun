(ns perepisun.http-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [clj-http.client :refer [post]]
   [integrant.core :as ig]
   [jsonista.core :as json]
   [taoensso.timbre :as log]
   [perepisun.config :refer [read-config]]
   [perepisun.fixtures :as f]
   [perepisun.redis :as redis]
   [perepisun.system :as system]))

(def config (read-config))

(def srv-url (str "http://localhost:"
                  (-> config :webserver :port)
                  "/api/"
                  (:bot-api-key config)))

(defmethod ig/init-key :app/mock-db [_ {state :state}]
  (reify redis/DB
    (get-mappings [_ chat-id]
      (reset! state chat-id))))

(def system system/system)

(defn make-tg-send-message-mock [state]
  (fn [_ chat-id text]
    (reset! state {:chat-id chat-id :text text})))

(deftest show-command
  (testing "/show command handling"
    (let [db-state (atom 0)
          tg-atom (atom {})
          tg-mock (make-tg-send-message-mock tg-atom)
          system (-> system
                     (assoc-in [:handler/show :db] (ig/ref :app/mock-db))
                     (assoc :app/mock-db {:state db-state})
                     (assoc-in [:handler/show :tg-send-message] tg-mock)
                     ig/init)]
      (try
        (let [resp (post srv-url {:content-type :json
                                  :body (json/write-value-as-string f/command-show)})]
          (is (= 200 (:status resp)))
          (is (= -1001491248945 @db-state) "correct chat-id in redis call")
          (is (= -1001491248945 (:chat-id @tg-atom)) "correct chat-id in telegram API call"))
        (finally (ig/halt! system))))))

(deftest valid-message
  (testing "it correctly responds to http request"
    (let [system (ig/init system)]
      (try
        (let [resp (post srv-url
                         {:content-type :json
                          :body         (json/write-value-as-string f/msg-with-only-location)})]
          (is (= 200 (:status resp)))
          (resp (= "all ok" (:body resp))))
        (finally (ig/halt! system))))))

(deftest new-chat-member
  (testing "it correctly responds to http request"
    (let [srv (ig/init system)]
      (try
        (let [resp (post srv-url
                         {:content-type :json
                          :body         (json/write-value-as-string f/new-member)})]
          (is (= 200 (:status resp)))
          (is (= "all ok" (:body resp))))
        (finally (ig/halt! srv))))))

