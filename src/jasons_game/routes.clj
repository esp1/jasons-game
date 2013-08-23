(ns jasons-game.routes
  (:use compojure.core)
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [file reader]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.format-response :refer [wrap-clojure-response]]))

(defn load-world []
  (with-open [in (java.io.PushbackReader. (reader "server-resources/world.edn"))]
    (let [edn-seq (repeatedly (partial edn/read {:eof :theend} in))]
      (pr-str (take-while (partial not= :theend) edn-seq)))))
  

(defn get-resource [kind type encoding id]
  (let [f (file (str "server-resources/" kind) (str id "." type "." encoding))]
    (when (.exists f)
      (slurp f))))


;; Routes

(defroutes app-routes
  (GET "/world" [] (load-world))
  (GET "/:kind/:type/:encoding/:id" [kind type encoding id] (get-resource kind type encoding id))
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
