(ns jasons_game.routes
  (:use compojure.core)
  (:require [clojure.java.io :refer [file reader]]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn get-image [id]
  (let [f (file "resources/public" (str id ".base64"))]
    (when (.exists f)
      (let [ext (last (clojure.string/split id #"\."))]
        (str "data:image/" ext ";base64," (slurp f))))))

(defroutes app-routes
  (GET "/image/:id" [id] (get-image id))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-redirects [handler]
  (fn [req]
    (handler
      (update-in req [:uri]
                 #(case %
                    "/" "/index.html"
                    "/repl" "/repl.html"
                    %)))))

(def app
  (wrap-redirects (handler/site app-routes)))
