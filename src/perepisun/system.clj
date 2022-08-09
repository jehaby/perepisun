(ns perepisun.system
  (:require
   [integrant.core :as ig]
   [perepisun.config]
   [perepisun.handlers]
   [perepisun.handlers.rewrite]
   [perepisun.http]
   [perepisun.redis]
   [perepisun.telegram]
   [taoensso.timbre.tools.logging]
   [telegrambot-lib.core :as tbot]))

(def system
  {:webserver {:config (ig/ref :app/config)
               :handler/rewrite (ig/ref :handler/rewrite)
               :handler/help (ig/ref :handler/help)
               :handler/show (ig/ref :handler/show)
               :handler/set (ig/ref :handler/set)}

   :app/config {}

   :app/redis {:config (ig/ref :app/config)}

   :app/tbot {:config (ig/ref :app/config)}

   :handler/help {:tbot (ig/ref :app/tbot)
                  :tg-send-message tbot/send-message}

   :handler/rewrite {:tbot (ig/ref :app/tbot)
                     :tg-send-message tbot/send-message
                     :tg-delete-message tbot/delete-message
                     :db (ig/ref :app/redis)}

   :handler/show {:tbot (ig/ref :app/tbot)
                  :tg-send-message tbot/send-message
                  :db (ig/ref :app/redis)}

   :handler/set {:tbot (ig/ref :app/tbot)
                 :tg-send-message tbot/send-message
                 :db (ig/ref :app/redis)}})
