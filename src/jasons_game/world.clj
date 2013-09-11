(ns jasons-game.world
  (:require [jasons-game.core :refer [resolve-aliases text]]
            [jasons-game.thing :as thing]))

;; Things

(defn get-things
  [world]
  (:things @world))

(defn add-thing
  "Adds a thing to the world. The thing will be wrapped in an atom."
  [world thing]
  (swap! world update-in [:things] conj (atom thing)))

(defn modify-thing
  "Changes an attribute of a thing"
  [thing attr value]
  (swap! thing assoc attr value))


;; World
;; {:things [ ... ]  ; z-ordered things
;;  :utterance { :context { ... }
;;               :sentence { ... } }}

(defn new-world
  ([& things] (let [world (atom {})]
                (doseq [thing things]
                  (add-thing world thing))
                world)))

(def the-world (new-world))

(defn get-thing-at-location
  [world location]
  (first (filter #(thing/contains-location (deref %) location) (:things @world))))


(defn say-something [context utterance]
  (swap! the-world assoc :utterance (merge {:context context} utterance)))
