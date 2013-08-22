(ns jasons-game.ui
  (:require-macros [dommy.macros :refer [sel1]])
  (:require [clojure.browser.repl :as repl]
            [jasons-game.draw :as d]
            [jasons-game.thing.person]
            [jasons-game.speech-balloon :refer [say]]
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


;; Sketch

(def world (w/new-world))

(defn setup []
  (s/size (.-innerWidth js/window) (.-innerHeight js/window))
  (s/text-font (s/create-font "Arial" 32))
  (w/populate-world world))

(defn draw []
  (let [mx (s/mouse-x), my (s/mouse-y)]
    (s/background 100)
    
    (doseq [thing (w/get-contents world)]
      (d/draw-thing thing)
      (say thing (str "My name is " (thing :name))))
    
    (draw-cursor mx my)))

(defn init []
  (repl/connect "http://localhost:9000/repl")
  (js/Processing. (sel1 :#stage) (s/sketch-init {:setup setup
                                                 :draw draw})))
