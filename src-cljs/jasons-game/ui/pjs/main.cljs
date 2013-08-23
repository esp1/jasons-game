(ns jasons-game.ui.pjs.main
  (:require-macros [dommy.macros :refer [sel1]]
                   [cljs.core.async.macros :as m :refer [go]])
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async :refer [<! go timeout]]
            [clojure.browser.repl :as repl]
            [clojure.string :refer [split]]
            [dommy.core :as dommy :refer [listen!]]
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


;; World

(def world (w/new-world))

(defn populate-world []
  (GET "/world" {:handler (fn [response]
                            (doseq [thing response]
                              (w/add-thing world thing)))
                 :error-handler (fn [response] (js/alert response))}))

(defn word-at-a-time [words]
  (reverse (map (comp #(clojure.string/join " " %) reverse) (take-while #(< 0 (count %)) (iterate rest (reverse (split words #"\W")))))))

(defn say [thing words]
  (w/add-thing world {:type :word-balloon
                                  :name :word-balloon
                                  :location (let [[x y] (:location thing)
                                                  [x0 y0 w h] (t/bounds-in-local thing)]
                                              [x (+ y y0)])  ; posiiton word balloon over top center of thing
                                  :words ""})
  (let [athing (w/get-thing world :word-balloon)]
    (go
      (doseq [w (word-at-a-time words)]
        (w/modify-thing athing :words w)
        (<! (timeout 500))))))


;; Sketch

(defn setup []
  (s/size (.-innerWidth js/window) (.-innerHeight js/window))
  (s/text-font (s/create-font "Arial" 32))
  (populate-world))

(defn draw []
  (let [mx (s/mouse-x), my (s/mouse-y)]
    (s/background 100)
    
    (doseq [thing (w/get-contents world)]
      (t/draw @thing))
    
    (draw-cursor mx my)))

(defn mouse-released []
  (when-let [thing (w/get-thing-at-location world [(s/mouse-x) (s/mouse-y)])]
    (say @thing (str "My name is " (:name @thing))))
  (GET "/audio/ogg/base64/sound_test" {:handler (fn [response]
                                                  (dommy/append! (sel1 :body) [:audio {:autoplay true}
                                                                               [:source {:src (str "data:audio/ogg;base64," response), :type "audio/ogg"}]]))
                                       :error-handler (fn [response] (js/alert response))}))

(defn mouse-dragged []
  (let [mx (s/mouse-x)
        my (s/mouse-y)]
    (when-let [thing (w/get-thing-at-location world [mx my])]
      (w/move-thing thing [mx my]))))

(defn init []
  (repl/connect "http://localhost:9000/repl")
  
  (js/Processing. (sel1 :#stage) (s/sketch-init {:setup setup
                                                 :draw draw
                                                 :mouse-released mouse-released
                                                 :mouse-dragged mouse-dragged})))
