(ns perepisun.handlers-test
  (:require [clojure.test :refer :all]
            [perepisun.handlers :as sut]))

(deftest respond-test
  (is (= "вტф" (sut/respond "втф" {"т" "ტ"})))
  (is (= "ჯони" (sut/respond "джони" {"дж" "ჯ"}))))
