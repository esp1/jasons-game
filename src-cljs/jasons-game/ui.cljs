(ns jasons-game.ui
  (:require-macros [dommy.macros :refer [sel sel1]])
  (:require [jasons-game.world :as world]
            [clojure.browser.repl :as repl]
            [clojure.string :refer [split]]
            [ajax.core :refer [GET]]
            [dommy.core :as dommy]
            [libre.sketch :as s]))

;; Styles

(def styles {:cursor {:stroke 0}
             :person {:stroke 0, :fill [0 255 0]}
             :speech-balloon {:fill [0 0 255]}
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

(defn set-text-box! [node] (dommy/replace-contents! (sel1 :#text-box) node))

(defn show-coords [x y]
  (set-text-box! [:div "x:" [:span.highlight x] ", y:" [:span.highlight y]]))


;; Sketch

(defn draw
  "Lookup and apply styles for named object, translate origin to given coordinate, and draw using supplied function"
  [name x y f]
  (s/push-style)
  (set-style name)
  (s/push-matrix)
  (s/translate x y)
  (f)  ; actually draw
  (s/pop-matrix)
  (s/pop-style))

(defn shape
  [vertices]
  (s/begin-shape)
  (doseq [vertex vertices]
    (apply s/vertex vertex))
  (s/end-shape))

(def cursor-size 10)
(defn draw-cursor
  "Draws a cursor at the specified coordinate"
  [x y]
  (draw :cursor x y
        (fn []
          (s/line (- cursor-size) 0, cursor-size 0)
          (s/line 0 (- cursor-size), 0 cursor-size))))

(def speech-balloon-properties {:offset 10
                                :point-width 20
                                :point-height 40})
(defn draw-speech-balloon
  "Draws a speech balloon over the specified coordinate"
  [speech x y]
  (let [p speech-balloon-properties
        balloon-width (+ (s/text-width speech) 40)
        balloon-height (+ (s/text-ascent) (s/text-descent) 40)]
    (draw :speech-balloon x y
          (fn []
            (s/translate (- (/ balloon-width 2))
                         (- (+ (p :offset) (p :point-height) balloon-height)))
            
            (shape [[0 0]
                    [balloon-width 0]
                    [balloon-width balloon-height]
                    [(+ (/ balloon-width 2) (p :point-width)) balloon-height]
                    [(/ balloon-width 2) (+ balloon-height (p :point-height))]
                    [(/ balloon-width 2) balloon-height]
                    [0 balloon-height]])
            
            (set-style :words)
            (s/text speech 20 (+ 20 (s/text-ascent)))))))

(def person-radius 100)
(defn draw-person
  "Draws a person at the specified coordinate"
  [x y]
  (draw :person x y
        (fn []
          (s/ellipse 0 0 person-radius person-radius)
          (draw-speech-balloon "How are you" 0 (- (/ person-radius 2))))))

(def images {})

(defn load-image [id]
  (if-let [img (images id)]
    (if (symbol? img)
      ; s/load-image on the base64 image data
      (let [ext (last (split id #"\."))
            url (str "data:image/" ext ";base64," img)
            loaded-image (s/load-image url)]
        (def images (assoc images id loaded-image)))
      ; return the cached image
      img)
    
    ; not loaded yet, go get it via alax call
    (GET (str "/image/" id) {:handler (fn [response]
                                        ; can't call s/load-image here because we're not in the dynamic scope for the *processing* binding
                                        (def images (assoc images id response)))})))  ; so just store the base64 image data in the map

(defn init []
  (js/Processing.
    (sel1 :#stage)
    (s/sketch-init {:setup (fn []
                             (s/size (.-innerWidth js/window) (.-innerHeight js/window))
                             (s/text-font (s/create-font "Arial" 32)))
                    
                    :draw (fn []
                            (let [mx (s/mouse-x), my (s/mouse-y)]
                              (s/background 100)
                              
                              (draw-person mx my)
                              
                              (s/push-matrix)
                              (s/scale 2.0)
                              (if-let [img (load-image "logo-color-255x75.png")]
                                (s/image img 100 100))
                              (s/pop-matrix)
                              
                              (draw-cursor mx my)
                              
                              (show-coords mx my)))}))
  
  ; Use of "localhost" will only work for local development.
  ; Change the port to match the :repl-listen-port.
  (repl/connect "http://localhost:9000/repl"))
