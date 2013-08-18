(ns jasons_game.ui
  (:require-macros [dommy.macros :refer [sel sel1]])
  (:require [dommy.utils :as utils]
            [dommy.core :as dommy]))

(defn text-box [] (sel1 :#text-box))

(defn set-text!
  [node]
  (dommy/replace-contents! (text-box) node))


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

(defn color [name] (to-color-array ((styles name) :color)))
(defn stroke [name] (to-color-array ((styles name) :stroke)))
(defn fill [name] (to-color-array ((styles name) :fill)))


;; Event handlers

(defn mouseMoved
  [sketch mouseButton mouseX mouseY pmouseX pmouseY]
  (set-text! [:div mouseButton "->" [:span.highlight mouseX] ":" [:span.highlight mouseY]]))

(defn mousePressed
  [sketch mouseButton mouseX mouseY pmouseX pmouseY]
  (.doThing sketch))
