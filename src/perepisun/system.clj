(ns perepisun.system
  (:require
   [integrant.core :as ig]
   [telegrambot-lib.core :as tbot]
   [perepisun.config :refer [config]]
   [perepisun.http]
   [perepisun.handlers.rewrite]
   [perepisun.tbot]))

(defmethod ig/init-key :app/tbot [_ {api-key :api-key}]
  (tbot/create api-key))

(def system
  {:webserver {:port (-> config :webserver :port)
               :handler/rewrite (ig/ref :handler/rewrite)}

   ;; :redis {:a :b}

   :handler/rewrite {:tbot (ig/ref :app/tbot)
                     ;; :redis (ig/ref :redis)
                     }

   :app/tbot {:api-key (:bot-api-key config)}})
