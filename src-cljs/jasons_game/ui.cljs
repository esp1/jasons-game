(ns jasons_game.ui
  (:require-macros [dommy.macros :refer [sel sel1]])
  (:require [dommy.utils :as utils]
            [dommy.core :as dommy]))

(defn text-box [] (sel1 :#text-box))

(defn set-text!
  [node]
  (dommy/replace-contents! (text-box) node))

(defn mouseMoved
  [sketch mouseButton mouseX mouseY pmouseX pmouseY]
  (set-text! [:div mouseButton "->" [:span.highlight mouseX] ":" [:span.highlight mouseY]]))

(defn mousePressed
  [sketch mouseButton mouseX mouseY pmouseX pmouseY]
  (.doThing sketch))
