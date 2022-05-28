(ns perepisun.fixtures)

(def msg-with-only-location
  {:update_id 555827554,
   :message
   {:date       1649517992,
    :chat
    {:username "rewraktar_ge",
     :type     "supergroup",
     :title    "rewraktar-test",
     :id       -1001491248945},
    :message_id 14,
    :from
    {:first_name "Stas",
     :is_bot     false,
     :username   "jehaby",
     :id         146480146,
     :last_name  "Makarov"},
    :location   {:longitude 44.797993, :latitude 41.717148}}})

(def msg-only-photo
  {:update_id 555827555,
   :message
   {:date       1649518089,
    :chat
    {:username "rewraktar_ge",
     :type     "supergroup",
     :title    "rewraktar-test",
     :id       -1001491248945},
    :message_id 15,
    :photo
    [{:width          90,
      :file_size      1661,
      :file_unique_id "AQADSboxGxVGkEp4",
      :file_id
      "AgACAgIAAx0CWOKnMQADD2JRpglyitJWcU6I7L2aUVLZvdziAAJJujEbFUaQSmS586ApxrgAAQEAAwIAA3MAAyME",
      :height         67}
     {:width          320,
      :file_size      29568,
      :file_unique_id "AQADSboxGxVGkEpy",
      :file_id
      "AgACAgIAAx0CWOKnMQADD2JRpglyitJWcU6I7L2aUVLZvdziAAJJujEbFUaQSmS586ApxrgAAQEAAwIAA20AAyME",
      :height         240}
     {:width          800,
      :file_size      145448,
      :file_unique_id "AQADSboxGxVGkEp9",
      :file_id
      "AgACAgIAAx0CWOKnMQADD2JRpglyitJWcU6I7L2aUVLZvdziAAJJujEbFUaQSmS586ApxrgAAQEAAwIAA3gAAyME",
      :height         600}
     {:width          1280,
      :file_size      236804,
      :file_unique_id "AQADSboxGxVGkEp-",
      :file_id
      "AgACAgIAAx0CWOKnMQADD2JRpglyitJWcU6I7L2aUVLZvdziAAJJujEbFUaQSmS586ApxrgAAQEAAwIAA3kAAyME",
      :height         960}],
    :from
    {:first_name "Stas",
     :is_bot     false,
     :username   "jehaby",
     :id         146480146,
     :last_name  "Makarov"}}})

(def bot-commands
  {:update_id 555827594,
   :message
   {:date       1649552048,
    :entities   [{:offset 0, :type "bot_command", :length 23}],
    :chat
    {:username "rewraktar_ge",
     :type     "supergroup",
     :title    "rewraktar-test",
     :id       -1001491248945},
    :message_id 80,
    :from
    {:first_name "Stas",
     :is_bot     false,
     :username   "jehaby",
     :id         146480146,
     :last_name  "Makarov"},
    :text       "/show@rewraktar_dev_bot"}})


(def new-member
  {:update_id 835717283,
   :message
   {:date       1649859837,
    :new_chat_members
    [{:first_name "Pauline",
      :is_bot     false,
      :username   "polisola",
      :id         508943019}],
    :new_chat_participant
    {:first_name "Pauline", :is_bot false, :username "polisola", :id 508943019},
    :chat
    {:type                           "group",
     :title                          "Ponyard с грузинским алфавитом",
     :id                             -786766343,
     :all_members_are_administrators true},
    :message_id 786,
    :new_chat_member
    {:first_name "Pauline", :is_bot false, :username "polisola", :id 508943019},
    :from
    {:first_name "Pauline", :is_bot false, :username "polisola", :id 508943019}}})
