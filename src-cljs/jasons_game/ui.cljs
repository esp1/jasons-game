(ns jasons_game.ui
  (:require-macros [dommy.macros :refer [sel sel1]])
  (:require [dommy.core :as dommy]))


;; Styles

(def styles {"actor" {:stroke 0, :fill [0, 0, 255]}})

(defn to-color-array
  "Convert color spec to color array"
  ([] nil)
  ([color] (cond
             (coll? color) (case (count color)
                             3 (array (color 0) (color 1) (color 2) 255)
                             4 (array (color 0) (color 1) (color 2) (color 3)))
             (number? color) (array color color color 255))))

(defn color-for-name [name] (to-color-array ((styles name) :color)))
(defn stroke-for-name [name] (to-color-array ((styles name) :stroke)))
(defn fill-for-name [name] (to-color-array ((styles name) :fill)))


;; Text box

(defn text-box [] (sel1 :#text-box))

(defn set-text!
  [node]
  (dommy/replace-contents! (text-box) node))

(defn show-mouse-coords [mouse-button mouse-x mouse-y]
  (set-text! [:div mouse-button "->" [:span.highlight mouse-x] ":" [:span.highlight mouse-y]]))
