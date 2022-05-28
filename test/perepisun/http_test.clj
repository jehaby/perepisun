(ns perepisun.http-test
  (:require [clojure.test :refer :all]
            [clj-http.client :refer [post]]
            [perepisun.config :refer [config]]
            [perepisun.fixtures :as f]
            [perepisun.http :as sut]
            [jsonista.core :as json]))

(def srv-url (str "http://localhost:"
                  (-> config :webserver :port)
                  "/api/"
                  (:bot-api-key config)))

(defn make-test-system [] )

(deftest valid-message
  (testing "it correctly responds to http request"
    (let [srv (sut/start-server nil)]
      (try
        (let [resp (post srv-url
                         {:content-type :json
                          :body         (json/write-value-as-string f/msg-with-only-location)})]
          (is (= 200 (:status resp)))
          (is (= "all ok" (:body resp))))
        (finally (.stop srv))))))

(deftest new-chat-member
  (testing "it correctly responds to http request"
    (let [srv (sut/start-server nil)]
      (try
        (let [resp (post srv-url
                         {:content-type :json
                          :body         (json/write-value-as-string f/new-member)})]
          (is (= 200 (:status resp)))
          (is (= "all ok" (:body resp))))
        (finally (.stop srv))))))

(comment
  (defonce s (sut/start-server nil))
  (.start s)
  (.stop s)
  (post srv-url
        {:content-type :json
         :body         (json/write-value-as-string f/simple-message)})

  ;; TODO test http handler, replace, respond
  )


