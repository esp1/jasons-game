(ns jasons-game.ui.pjs.word-balloon
  (:require [clojure.zip :as zip]
            [jasons-game.core :refer [sentence-zipper text word?]]
            [jasons-game.ui.pjs.draw :as d]
            [libre.sketch :as s]))

(def offset 10)
(def point-width 20)
(def point-height 40)

(defn draw-text
  [loc x-offset y-offset]
  (cond
    (zip/end? loc) nil  ; stop
    (word? loc) (let [node (zip/node loc)
                      t (:text node)
                      fill (condp :style node
                             :highlight [200 0 0]
                             :normal 100
                             255)]
                  (js/alert (str t " " fill))
                  (d/style {:fill fill}
                           #(s/text t x-offset y-offset))
                  (recur (zip/next loc) (+ x-offset (s/text-width (str t " "))) y-offset))
    :else (recur (zip/next loc) x-offset y-offset)))

(defn draw
  [location sentence]
  (d/translate location
               (let [words (text sentence)
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
                   (draw-text (sentence-zipper sentence) 20 (+ 20 (s/text-ascent)))))))
