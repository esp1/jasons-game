(ns jasons-game.thing.speech-balloon
  (:require [jasons-game.draw :as d]
            [libre.sketch :as s]))

(def offset 10)
(def point-width 20)
(def point-height 40)

(defn draw-speech-balloon
  "Draws a speech balloon over the specified coordinate"
  [speech x y]
  (let [balloon-width (+ (s/text-width speech) 40)
        balloon-height (+ (s/text-ascent) (s/text-descent) 40)]
    (d/translate [x y]
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
                              (s/text speech 20 (+ 20 (s/text-ascent)))))))))
