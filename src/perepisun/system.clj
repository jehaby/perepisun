(ns perepisun.system
  (:require
   [integrant.core :as ig]
   [telegrambot-lib.core :as tbot]
   [perepisun.config]
   [perepisun.http]
   [perepisun.redis]
   [perepisun.handlers]
   [perepisun.handlers.rewrite]))

(defmethod ig/init-key :app/tbot [_ {config :config}]
  (tbot/create (-> config :bot-api-key)))

(def system
  {:webserver {:config (ig/ref :app/config)
               :handler/rewrite (ig/ref :handler/rewrite)
               :handler/help (ig/ref :handler/help)
               :handler/show (ig/ref :handler/show)
               :handler/set (ig/ref :handler/set)}

   :app/config {}

   :app/redis {:config (ig/ref :app/config)}

   :handler/help {:tbot (ig/ref :app/tbot)}

   :handler/rewrite {:tbot (ig/ref :app/tbot)
                     :db (ig/ref :app/redis)}

   :handler/show {:tbot (ig/ref :app/tbot)
                  :db (ig/ref :app/redis)}

   :handler/set {:tbot (ig/ref :app/tbot)
                 :db (ig/ref :app/redis)}

   :app/tbot {:config (ig/ref :app/config)}})
