(ns jasons-game.ui.pjs.main
  (:require-macros [dommy.macros :refer [sel1]]
                   [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [<! go timeout]]
            [clojure.browser.repl]
            [clojure.zip :as zip]
            [dommy.core :as dommy]
            [jasons-game.core :refer [sentence-zipper style-highlight style-normal text word?]]
            [jasons-game.thing :as thing]
            [jasons-game.ui.pjs.draw :as draw]
            [jasons-game.ui.util :refer [ch-get ch-post]]
            [jasons-game.ui.pjs.word-balloon :as word-balloon]
            [jasons-game.world :as world]
            [libre.sketch :as s]))

;; World

(def me {:name "Jason"})


;; Word balloon

(defn animate-utterance []
  (go
    (doseq [loc (map style-highlight  ; highlight loc
                     (filter word?  ; only get word locs
                             (take-while #(not (zip/end? %))  ; stop at end
                                         (iterate (comp zip/next style-normal)  ; apply normal style to each loc
                                                  (sentence-zipper (:utterance @world/the-world))))))]
      (swap! world/the-world assoc :utterance (zip/root loc))
      (<! (timeout 500)))))

(defn play-audio [sentence]
  (let [audio-file (js/encodeURIComponent (text sentence))]
    (go (let [audio-data (<! (ch-get (str "/audio/ogg/base64/" audio-file)))]
          (dommy/replace! (sel1 :#audio)
                          [:audio {:id "audio", :autoplay true}
                           [:source {:src (str "data:audio/ogg;base64," audio-data), :type "audio/ogg"}]])))))

(defn say [context]
  (go (let [sentence (<! (ch-get "/something-to-say"))]
        (world/say-something context sentence)
        (animate-utterance)
        (play-audio sentence))))


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
         (go (let [things (<! (ch-get "/world"))]
               (doseq [thing things]
                 (world/add-thing world/the-world thing)))))
       
       :draw
       (fn []
         (let [mx (s/mouse-x), my (s/mouse-y)]
           (s/background 100 200 255)
           
           (doseq [thing (world/get-things world/the-world)]
             (thing/draw @thing))

           (when-let [utterance (:utterance @world/the-world)]
             ; draw a world bubble over the speaker
             (let [speaker (get-in utterance [:context :speaker])
                   [x y] (:location speaker)
                   [x0 y0 w h] (thing/bounds-in-local speaker)]
               (word-balloon/draw [x (+ y y0)]  ; position word balloon over top center of speaker
                                  utterance)))
           
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
             (world/modify-thing thing :location [mx my]))))
       
       :key-pressed
       (fn []
         (let [key (.toString (s/get-key))]
           (when (= key "s")  ; Save
             (go (let [response (<! (ch-post "/world" {:world (pr-str (map deref (world/get-things world/the-world)))}))]
                    (js/alert response))))))})))
