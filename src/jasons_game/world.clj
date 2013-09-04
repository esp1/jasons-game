(ns jasons-game.world
  (:require [jasons-game.core :refer [resolve-aliases text]]
            [jasons-game.thing :as thing]))

;; Things

(defn get-thing
  [world name]
  (@world name))

(defn add-thing
  "Adds a thing to the world. The thing will be wrapped in an atom."
  [world thing]
  (swap! world assoc (:name thing) (atom thing)))

(defn remove-thing
  "Removes a thing from the world"
  [world name]
  (swap! world dissoc name))

(defn move-thing
  "Changes the location of a thing"
  [thing location]
  (swap! thing assoc :location location))

(defn modify-thing
  "Changes an attribute of a thing"
  [thing attr value]
  (swap! thing assoc attr value))


;; World
; The world is an ordered map of objects.
; The order of the objects indicates their z-ordering when they are drawn.
(defn new-world
  ([& things] (let [world (atom (sorted-map))]
                (doseq [thing things]
                  (add-thing world thing))
                world)))

(def the-world (new-world))

(defn get-contents
  [world]
  (vals @world))

(defn get-thing-at-location
  [world location]
  (first (filter #(thing/contains-location (deref %) location) (get-contents world))))


;; Word balloon

(defn create-word-balloon [env sentence]
  (let [words (resolve-aliases (text sentence) env)]
    ; add word balloon to the world
    (add-thing the-world {:type :word-balloon
                          :name :word-balloon
                          :location (let [speaker (:speaker env)
                                          [x y] (:location speaker)
                                          [x0 y0 w h] (thing/bounds-in-local speaker)]
                                      [x (+ y y0)])  ; posiiton word balloon over top center of thing
                          :words ""})))
