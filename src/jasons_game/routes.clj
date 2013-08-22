(ns jasons-game.routes
  (:use compojure.core)
  (:require [clojure.java.io :refer [file reader]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.format-response :refer [wrap-clojure-response]]))

(defn get-image [id]
  (let [f (file "resources/public/img" (str id ".base64"))]
    (when (.exists f)
      (slurp f))))

(defroutes app-routes
  (GET "/world" [] (pr-str '( {:type :person
                               :name "Edwin"
                               :location [400 200]}
                              {:type :person
                               :name "Christine"
                               :location [700 300]} )))
  (GET "/image/:id" [id] (get-image id))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-redirects [handler]
  (fn [req]
    (handler
      (update-in req [:uri]
                 #(case %
                    "/" "/index.html"
                    %)))))

(def app
  (-> (handler/site app-routes)
    wrap-redirects
    wrap-clojure-response))
