(ns perepisun.http
  (:require
   [ring.middleware.params :as params]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [muuntaja.core :as m]
   [reitit.ring.coercion :as coercion]
   [reitit.ring :as ring]
   [taoensso.timbre.tools.logging]
   [ring.adapter.jetty9 :refer [run-jetty]]
   [taoensso.timbre :as log]
   [telegrambot-lib.core :as tbot]
   [perepisun.config :refer [config]]
   [perepisun.handlers :as h]
   [clojure.string :as str])
  (:gen-class))


(defn make-handler [system]
  (fn [{event :body-params :as req}]
    ;; (def req req)
    ;; (def event event)
    (log/debug "got event " event)
    (try
      (when-let [text (-> event :message :text)]
        ;; check that event is a message
        (condp #(str/starts-with? %2 %1) text ;; [1]
          "/help"   (h/help event)
          "/start"  (h/todo event) #_ (log/debug "got start " event)
          "/stop"   (h/todo event) #_ (log/debug "got stop " event)
          "/status" (h/todo event) #_ (h/status event)
          "/show"   (h/show event)
          "/set"    (h/set event) ;; TODO: change to set
          "/delete" (h/todo event)
          ;; nil       {:status 200}  #_ (h/delete event)
          (h/rewrite-handler event))) ;; TODO: check this case, is it the source of errors?
      (catch Exception e
        (log/error {:msg "excpetion in handler" :err e :event event})
        {:status 200 :body (str "got error" e)}
        )
      )
    {:status 200 :body "all ok"}
    ))

(defn make-app [system]
  (ring/ring-handler
    (ring/router
      ["/api"
       ["/:token"
        {:post (make-handler system)
         }]]
      {:data {:muuntaja   m/instance
      	      :middleware [params/wrap-params
                           muuntaja/format-middleware
                           coercion/coerce-exceptions-middleware
                           coercion/coerce-request-middleware
                           coercion/coerce-response-middleware]}})
    (ring/create-default-handler)))

(defn start-server [system]
  (log/info "config: " config)
  (let [port   (-> config :webserver :port)
        server (run-jetty (make-app system) {:port port :join? false})]
    (try
      (.start server)
      (catch Exception e
        (log/fatal "couldn't start the server" e)
        ))
    (log/info "server running in port " port)
    server))

(defn -main [& _args]
  (taoensso.timbre.tools.logging/use-timbre)
  (log/set-level! (:log-level config))
  (start-server nil))

;; [1] TODO it might be more effective to split by \w once, check if string
;; corresponds to a command, and after that call handler.
;; use (-> message :entities (nth 0) :type) = "bot_command"

(defonce srv (atom nil))
(comment
  (do
    (when @srv (.stop @srv))
    (reset! srv (start-server nil))
    ))


