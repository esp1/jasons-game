(ns jasons-game.ui.pjs.main
  (:require-macros [dommy.macros :refer [sel1]]
                   [cljs.core.async.macros :as m :refer [go]])
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :as async :refer [<! go timeout]]
            [clojure.browser.repl]
            [dommy.core :as dommy]
            [jasons-game.core :refer [text]]
            [jasons-game.thing :as thing]
            [jasons-game.ui.pjs.draw :as draw]
            [jasons-game.world :as world]
            [libre.sketch :as s]))

(defn alert-handler [response]
  (js/alert response))


;; World

(def me {:name "Jason"})


;; Word balloon

(defn word-at-a-time [words]
  (reverse (map (comp #(clojure.string/join " " %) reverse) (take-while #(< 0 (count %)) (iterate rest (reverse (clojure.string/split words #"\s")))))))

(defn animate-text [words]
  (let [athing (world/get-thing world/the-world :word-balloon)]
    (go
      (doseq [w (word-at-a-time words)]
        (world/modify-thing athing :words w)
        (<! (timeout 500))))))

(defn play-audio [sentence]
  (let [audio-file (js/encodeURIComponent (text sentence))]
    (GET (str "/audio/ogg/base64/" audio-file) {:handler (fn [response]
                                                           (dommy/replace! (sel1 :#audio)
                                                                           [:audio {:id "audio", :autoplay true}
                                                                            [:source {:src (str "data:audio/ogg;base64," response), :type "audio/ogg"}]]))
                                                :error-handler alert-handler})))

(defn say-something [env sentence]
  (world/create-word-balloon env sentence)
  (animate-text (text sentence))
  (play-audio sentence))

(defn say [env]
  (GET "/something-to-say" {:handler #(say-something env %)
                            :error-handler alert-handler}))


;; Sketch

(defn init []
  (clojure.browser.repl/connect "http://localhost:9000/repl")
  
  (js/Processing.
    (sel1 :#stage)
    (s/sketch-init
      {:setup
       (fn []
         (s/size 1000 600)
         (s/text-font (s/create-font "Arial" 32))
         (GET "/world" {:handler (fn [response]
                                   (doseq [thing response]
                                     (world/add-thing world/the-world thing)))
                        :error-handler alert-handler}))
       
       :draw
       (fn []
         (let [mx (s/mouse-x), my (s/mouse-y)]
           (s/background 100 200 255)
           
           (doseq [thing (world/get-contents world/the-world)]
             (thing/draw @thing))
           
           (draw/draw-cursor mx my)))
       
       :mouse-released
       (fn []
         (when-let [thing (world/get-thing-at-location world/the-world [(s/mouse-x) (s/mouse-y)])]
           (say {:speaker @thing
                 :audience me})))
       
       :mouse-dragged
       (fn []
         (let [mx (s/mouse-x)
               my (s/mouse-y)]
           (when-let [thing (world/get-thing-at-location world/the-world [mx my])]
             (world/move-thing thing [mx my]))))
       
       :key-pressed
       (fn []
         (let [key (.toString (s/get-key))]
           (when (= key "s")
             (POST "/world" {:params {:world (pr-str (map deref (vals @world/the-world)))}
                             :handler alert-handler
                             :error-handler #(js/alert (pr-str "Save error:" %))}))))})))
