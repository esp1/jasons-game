(ns jasons-game.routes
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [file reader]]
            [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.format-response :refer [wrap-clojure-response]]))

;; Load/Save world state

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


;; Media resources

(defn get-resource
  [kind type encoding id]
  (let [r (str (java.net.URLDecoder/decode id) "." type)
        f (file (str "server-resources/" kind) (if encoding
                                                 (str r "." encoding)
                                                 r))]
    (when (.exists f)
      (slurp f))))


;; Conversation resources

(defn something-to-say []
  (pr-str (edn/read-string (slurp "server-resources/things-to-say.edn"))))


;; Routes

(defroutes app-routes
  ; load/save world state
  (GET "/world" [] (load-world))
  (POST "/world" [world] (save-world world))
  
  ; media resources
  (GET "/:kind/:type/:id" [kind type encoding id] (get-resource kind type nil id))
  (GET "/:kind/:type/:encoding/:id" [kind type encoding id] (get-resource kind type encoding id))
  
  ; conversation resources
  (GET "/something-to-say" [] (something-to-say))
  
  ; default routes
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
