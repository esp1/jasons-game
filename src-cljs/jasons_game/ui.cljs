(ns jasons_game.ui
  (:require-macros [dommy.macros :refer [sel sel1]])
  (:require [dommy.core :as dommy]
            [libre.sketch :as s]))


;; Styles

(def styles {:cursor {:stroke 0}
             :actor {:stroke 0, :fill [255, 0, 0]}
             :speech-balloon {:fill [0, 0, 255]}
             :words {:fill 255}})

(defn to-processing-color
  "Converts a color spec to a processing color, which is represented by a native javascript color array.
  A color spec can have any of the following forms:
  
  [ R G B A ] -> [ R G B A ]
  [ R G B ]   -> [ R G B 255 ]
  N           -> [ N N N 255 ]"
  ([] nil)
  ([color] (cond
             (coll? color) (case (count color)
                             4 (array (color 0) (color 1) (color 2) (color 3))
                             3 (array (color 0) (color 1) (color 2) 255))
             (number? color) (array color color color 255))))

(defn stroke-for [name] (to-processing-color ((styles name) :stroke)))
(defn fill-for [name] (to-processing-color ((styles name) :fill)))

(defn set-style [name]
  (if-let [[r g b a] (stroke-for name)]
    (s/stroke r g b a)
    (s/no-stroke))
  (if-let [[r g b a] (fill-for name)]
    (s/fill r g b a)
    (s/no-fill)))


;; Text box

(defn text-box [] (sel1 :#text-box))

(defn set-text-box! [node] (dommy/replace-contents! (text-box) node))

(defn show-coords [x y]
  (set-text-box! [:div "x:" [:span.highlight x] ", y:" [:span.highlight y]]))


;; Sketch

(defn draw-begin [name x y]
  (s/push-style)
  (set-style name)
  (s/push-matrix)
  (s/translate x y))

(defn draw-end []
  (s/pop-matrix)
  (s/pop-style))

(def cursor-size 10)
(defn draw-cursor [x y]
  (draw-begin :cursor x y)
  (s/line (- cursor-size) 0, cursor-size 0)
  (s/line 0 (- cursor-size), 0 cursor-size)
  (draw-end))

(def offset 10)
(def point-width 20)
(def point-height 40)
(defn draw-speech-balloon [speech x y]
  (let [balloon-width (+ (s/text-width speech) 40)
        balloon-height (+ (s/text-ascent) (s/text-descent) 40)]
    (draw-begin :speech-balloon x y)
    (s/translate (- (/ balloon-width 2)) (- (+ offset point-height balloon-height)))
    
    (s/begin-shape)
    (s/vertex 0 0)
    (s/vertex balloon-width 0)
    (s/vertex balloon-width balloon-height)
    (s/vertex (+ (/ balloon-width 2) point-width) balloon-height)
    (s/vertex (/ balloon-width 2) (+ balloon-height point-height))
    (s/vertex (/ balloon-width 2) balloon-height)
    (s/vertex 0 balloon-height)
    (s/end-shape)
    
    (set-style :words)
    (s/text speech 20 (+ 20 (s/text-ascent)))
    
    (draw-end)))

(def actor-radius 100)
(defn draw-actor [x y]
  (draw-begin :actor x y)
  (s/ellipse 0 0 actor-radius actor-radius)
  (draw-speech-balloon "How are you" 0 (- (/ actor-radius 2)))
  (draw-end))

(def sketchy {:setup (fn []
                       (s/size (.-innerWidth js/window) (.-innerHeight js/window))
                       (s/text-font (s/create-font "Arial" 32)))
              :draw (fn []
                      (let [mx (s/mouse-x), my (s/mouse-y)]
                        (s/background 100)
                        
                        (draw-actor mx my)
                        (draw-cursor mx my)
                        
                        (show-coords mx my)))})

(defn init []
  (js/Processing. (sel1 :#world) (s/sketch-init sketchy)))
