(ns jasons-game.ui.pjs.word-balloon
  (:require [jasons-game.thing :as t]
            [jasons-game.ui.pjs.draw :as d]
            [libre.sketch :as s]))

(def offset 10)
(def point-width 20)
(def point-height 40)

(defmethod t/draw :word-balloon
  [word-balloon]
  (d/translate (:location word-balloon)
               (let [words (:words word-balloon)
                     balloon-width (+ (s/text-width words) 40)
                     balloon-height (+ (s/text-ascent) (s/text-descent) 40)]
                 (fn []
                   (s/translate (- (/ balloon-width 2))
                                (- (+ offset point-height balloon-height)))
                   
                   ; draw balloon
                   (d/style {:stroke 0, :fill 255}
                            (fn []
                              (d/shape [[0 0]
                                        [balloon-width 0]
                                        [balloon-width balloon-height]
                                        [(+ (/ balloon-width 2) point-width) balloon-height]
                                        [(/ balloon-width 2) (+ balloon-height point-height)]
                                        [(/ balloon-width 2) balloon-height]
                                        [0 balloon-height]])))
                   
                   ; draw text
                   (d/style {:fill 50}
                            (fn []
                              (s/text words 20 (+ 20 (s/text-ascent)))))))))

(defmethod t/bounds-in-local :word-balloon
  [word-balloon]
  [0 0 0 0])
