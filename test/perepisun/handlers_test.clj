(ns perepisun.handlers-test
  (:require [clojure.test :refer [deftest is]]
            [perepisun.handlers.rewrite :as sut]))

(deftest respond-test
  (is (= "вტф" (sut/respond "втф" {"т" "ტ"} "т")))
  (is (= "ჯони" (sut/respond "джони" {"дж" "ჯ"} "дж"))))
