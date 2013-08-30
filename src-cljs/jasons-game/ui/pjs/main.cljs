(ns jasons-game.ui.pjs.main
  (:require-macros [dommy.macros :refer [sel1]]
                   [cljs.core.async.macros :as m :refer [go]])
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :as async :refer [<! go timeout]]
            [clojure.browser.repl :as repl]
            [clojure.string :refer [replace split]]
            [dommy.core :as dommy :refer [listen!]]
            [jasons-game.core :refer [text]]
            [jasons-game.thing :as t]
            [jasons-game.ui.pjs.draw :as d]
            [jasons-game.world :as w]
            [libre.sketch :as s]))

;; Cursor

(def cursor-size 10)

(defn draw-cursor
  "Draws a cursor at the specified coordinate"
  [x y]
  ; draw crosshairs
  (d/translate [x y]
               {:stroke 0}
               (fn []
                 (s/line (- cursor-size) 0, cursor-size 0)
                 (s/line 0 (- cursor-size), 0 cursor-size)))
  ; display coordinates in text box
  (d/set-text-box! [:div "x:" [:span.highlight x] ", y:" [:span.highlight y]]))

(defn alert-handler [response]
  (js/alert response))


;; World

(def world (w/new-world))
(def me {:name "Jason"})

(defn populate-world []
  (GET "/world" {:handler (fn [response]
                            (doseq [thing response]
                              (w/add-thing world thing)))
                 :error-handler alert-handler}))

(defn word-at-a-time [words]
  (reverse (map (comp #(clojure.string/join " " %) reverse) (take-while #(< 0 (count %)) (iterate rest (reverse (split words #"\s")))))))

(defn play-audio [sentence]
  (let [audio-file (js/encodeURIComponent (text sentence))]
    (GET (str "/audio/ogg/base64/" audio-file) {:handler (fn [response]
                                                           (dommy/replace! (sel1 :#audio)
                                                                           [:audio {:id "audio", :autoplay true}
                                                                            [:source {:src (str "data:audio/ogg;base64," response), :type "audio/ogg"}]]))
                                                :error-handler alert-handler})))

(defn resolve-aliases [sentence env]
  (reduce
    (fn [s [key val]] (clojure.string/replace s (str ":" (name key)) (:name val)))
    sentence
    env))

(defn say-something [env sentence]
  (let [words (resolve-aliases (text sentence) env)]
    ; add word balloon to the world
    (w/add-thing world {:type :word-balloon
                        :name :word-balloon
                        :location (let [speaker (:speaker env)
                                        [x y] (:location speaker)
                                        [x0 y0 w h] (t/bounds-in-local speaker)]
                                    [x (+ y y0)])  ; posiiton word balloon over top center of thing
                        :words ""})
    
    ; animate word balloon text
    (let [athing (w/get-thing world :word-balloon)]
      (go
        (doseq [w (word-at-a-time words)]
          (w/modify-thing athing :words w)
          (<! (timeout 500)))))
    
    ; play audio
    (play-audio sentence)))

(defn say [env]
  (GET "/something-to-say" {:handler #(say-something env %)
                            :error-handler alert-handler}))
  

;; Sketch

(defn setup []
  (s/size 1000 600)
  (s/text-font (s/create-font "Arial" 32))
  (populate-world))

(defn draw []
  (let [mx (s/mouse-x), my (s/mouse-y)]
    (s/background 100 200 255)
    
    (doseq [thing (w/get-contents world)]
      (t/draw @thing))
    
    (draw-cursor mx my)))

(defn mouse-released []
  (when-let [thing (w/get-thing-at-location world [(s/mouse-x) (s/mouse-y)])]
    (say {:speaker @thing
          :audience me})))

(defn mouse-dragged []
  (let [mx (s/mouse-x)
        my (s/mouse-y)]
    (when-let [thing (w/get-thing-at-location world [mx my])]
      (w/move-thing thing [mx my]))))

(defn key-pressed []
  (let [key (.toString (s/get-key))]
    (when (= key "s")
      (POST "/world" {:params {:world (pr-str (map deref (vals @world)))}
                      :handler alert-handler
                      :error-handler #(js/alert (pr-str "Save error:" %))}))))

(defn init []
  (repl/connect "http://localhost:9000/repl")
  
  (js/Processing. (sel1 :#stage) (s/sketch-init {:setup setup
                                                 :draw draw
                                                 :mouse-released mouse-released
                                                 :mouse-dragged mouse-dragged
                                                 :key-pressed key-pressed})))
