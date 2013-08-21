(ns jasons-game.ui
  (:require-macros [dommy.macros :refer [sel1]])
  (:require [clojure.browser.repl :as repl]
            [jasons-game.draw :as d]
            [jasons-game.thing.person]
            [jasons-game.world :as world]
            [libre.sketch :as s]))

;; Sketch

(defn setup []
  (s/size (.-innerWidth js/window) (.-innerHeight js/window))
  (s/text-font (s/create-font "Arial" 32))
  (world/populate-world))

(defn draw []
  (let [mx (s/mouse-x), my (s/mouse-y)]
    (s/background 100)
    
    (doseq [thing (vals world/world)]
      (d/draw-thing thing))
    
    (d/draw-cursor mx my)))

(defn init []
  (repl/connect "http://localhost:9000/repl")
  (js/Processing. (sel1 :#stage) (s/sketch-init {:setup setup
                                                 :draw draw})))
