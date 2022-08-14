(ns perepisun.http
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [ring.adapter.jetty9 :refer [run-jetty]]
   [ring.middleware.params :as params]
   [taoensso.timbre :as log]
   [perepisun.handlers :as h]))

(defn tg-webhook-handler [{:handler/keys [about
                                          help
                                          rewrite
                                          set
                                          show
                                          start
                                          status
                                          stop]
                           :as _system}]
  (fn [{event :body-params :as _req}]
    (log/debug "got event " event)
    ;; (def system system)
    ;; (def event event)
    ;; (def _req _req)
    (try
      (when-let [text (-> event :message :text)]
        (let [handler (condp #(str/starts-with? %2 %1) text ;; [1]
                        "/about"  about
                        "/delete" h/todo
                        "/help"   help
                        "/set"    set
                        "/show"   show
                        "/start"  start
                        "/status" status
                        "/stop"   stop
                        rewrite)]
          (handler event)))
      (catch Exception e
        (log/error {:msg "excpetion in handler" :err e :event event})
        {:status 200 :body (str "got error" e)}))
    {:status 200 :body "all ok"}))

(defn ring-handler [system]
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/:token"
      {:post (tg-webhook-handler system)}]]
    {:data {:muuntaja   m/instance
            :middleware [params/wrap-params
                         muuntaja/format-middleware
                         coercion/coerce-exceptions-middleware
                         coercion/coerce-request-middleware
                         coercion/coerce-response-middleware]}})
   (ring/create-default-handler)))

(defn start-server [{config :config :as system}]
  (let [port   (-> config :webserver :port)
        server (run-jetty (ring-handler system) {:port port :join? false})]
    (try
      (.start server)
      (catch Exception e
        (log/fatal "couldn't start the server" e)))
    (log/info "server running on port " port)
    server))

(defmethod ig/init-key :webserver [_ opts]
  (start-server opts))

(defmethod ig/halt-key! :webserver [_ server]
  (.stop server))
