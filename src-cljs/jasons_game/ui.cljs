(ns jasons_game.ui
  (:require-macros [dommy.macros :refer [sel sel1]])
  (:require [dommy.core :as dommy]))


;; Styles

(def styles {"actor" {:stroke 0, :fill [255, 0, 0]}})

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


;; Text box

(defn text-box [] (sel1 :#text-box))

(defn set-text-box! [node] (dommy/replace-contents! (text-box) node))

(defn show-mouse-coords [mouse-button mouse-x mouse-y]
  (set-text-box! [:div mouse-button "->" [:span.highlight mouse-x] ":" [:span.highlight mouse-y]]))
