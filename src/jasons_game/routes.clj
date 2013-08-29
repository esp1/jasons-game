(ns jasons-game.routes
  (:use compojure.core)
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [file reader]]
            [clojure.pprint :refer [pprint]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.format-response :refer [wrap-clojure-response]]))

(defn something-to-say []
  (pr-str (edn/read-string (slurp "server-resources/things-to-say.edn"))))

(defn load-world []
  (with-open [in (java.io.PushbackReader. (reader "server-resources/world.edn"))]
    (let [edn-seq (repeatedly (partial edn/read {:eof :theend} in))]
      (pr-str (take-while (partial not= :theend) edn-seq)))))

(defn save-world [world]
  (let [writer (java.io.StringWriter.)]
    (doseq [obj (edn/read-string world)]
      (pprint obj writer))
    (spit "server-resources/world.edn" writer)
    "Saved"))

(defn get-resource
  [kind type encoding id]
  (let [r (str id "." type)
        f (file (str "server-resources/" kind) (if encoding
                                                 (str r "." encoding)
                                                 r))]
    (when (.exists f)
      (slurp f))))


;; Routes

(defroutes app-routes
  (GET "/something-to-say" [] (something-to-say))
  (GET "/world" [] (load-world))
  (POST "/save-world" [world] (save-world world))
  (GET "/:kind/:type/:id" [kind type encoding id] (get-resource kind type nil id))
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
