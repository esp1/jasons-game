(ns jasons-game.world)

;; World
; The world is a map of object names to objects.
(def world {})


;; Things

(defn add-thing
  "Add something to the world"
  [thing]
  (def world
    (assoc world (thing :name) thing)))

(defn remove-thing
  "Remove something from the world"
  [name]
  (def world
    (dissoc world name)))

(defn get-thing
  "Retrieve a named thing from the world"
  [name]
  (world name))

(defn move-thing
  "Move something in the world"
  [name location]
  (def world
    (assoc-in world [name :location] location)))


;; Population

(defn populate-world []
  (add-thing {:type :person
              :name "Edwin"
              :location [400 200]})
  (add-thing {:type :person
              :name "Christine"
              :location [700 300]}))
