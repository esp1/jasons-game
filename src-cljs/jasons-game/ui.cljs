(ns jasons-game.ui
  (:require-macros [dommy.macros :refer [sel1]])
  (:require [clojure.browser.repl :as repl]
            [jasons-game.draw :as d]
            [jasons-game.thing :as t]
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

(defn say [thing words]
  (w/add-thing world {:type :word-balloon
                      :name :word-balloon
                      :location (let [[x y] (:location thing)
                                      [x0 y0 w h] (t/bounds-in-local thing)]
                                  [x (+ y y0)])  ; posiiton word balloon over top center of thing
                      :words words}))


;; Sketch

(defn setup []
  (s/size (.-innerWidth js/window) (.-innerHeight js/window))
  (s/text-font (s/create-font "Arial" 32))
  (w/populate-world world))

(defn draw []
  (let [mx (s/mouse-x), my (s/mouse-y)]
    (s/background 100)
    
    (doseq [thing (w/get-contents world)]
      (t/draw @thing))
    
    (draw-cursor mx my)))

(defn mouse-pressed []
  (when-let [thing (w/get-thing-at-location world [(s/mouse-x) (s/mouse-y)])]
    (say @thing (str "My name is " (@thing :name)))))

(defn mouse-dragged[]
  (let [mx (s/mouse-x)
        my (s/mouse-y)]
    (when-let [thing (w/get-thing-at-location world [mx my])]
      (w/remove-thing world :word-balloon)
      (w/move-thing thing [mx my]))))

(defn init []
  (repl/connect "http://localhost:9000/repl")
  (js/Processing. (sel1 :#stage) (s/sketch-init {:setup setup
                                                 :draw draw
                                                 :mouse-pressed mouse-pressed
                                                 :mouse-dragged mouse-dragged})))
