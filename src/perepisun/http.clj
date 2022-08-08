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
   [taoensso.timbre.tools.logging]
   [perepisun.handlers :as h]))

(defn make-handler [{rewrite-hnd :handler/rewrite
                     help-hnd :handler/help
                     show-hnd :handler/show
                     set-hnd :handler/set
                     :as _system}]
  (fn [{event :body-params :as _req}]
    (log/debug "got event " event)
    (try
      (when-let [text (-> event :message :text)]
        ;; check that event is a message
        (condp #(str/starts-with? %2 %1) text ;; [1]
          "/help"   (help-hnd event)
          "/start"  (h/todo event) #_(log/debug "got start " event)
          "/stop"   (h/todo event) #_(log/debug "got stop " event)
          "/status" (h/todo event) #_(h/status event)
          "/show"   (show-hnd event)
          "/set"    (set-hnd event)
          "/delete" (h/todo event)
          ;; nil       {:status 200}  #_ (h/delete event)
          (rewrite-hnd event))) ;; TODO: check this case, is it the source of errors?
      (catch Exception e
        (log/error {:msg "excpetion in handler" :err e :event event})
        {:status 200 :body (str "got error" e)}))
    {:status 200 :body "all ok"}))

(defn make-app [system]
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/:token"
      {:post (make-handler system)}]]
    {:data {:muuntaja   m/instance
            :middleware [params/wrap-params
                         muuntaja/format-middleware
                         coercion/coerce-exceptions-middleware
                         coercion/coerce-request-middleware
                         coercion/coerce-response-middleware]}})
   (ring/create-default-handler)))

(defn start-server [{config :config :as system}]
  ;; (log/info "config: " config)
  (let [port   (-> config :webserver :port)
        server (run-jetty (make-app system) {:port port :join? false})]
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

(defn -main [& _args]
  (taoensso.timbre.tools.logging/use-timbre)
  ;; (log/set-level! (:log-level config))
  (start-server nil))
