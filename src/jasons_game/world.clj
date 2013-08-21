(ns jasons-game.world)

;; World
; The world is a map of object names to objects.
(def world {})


;; Things

(defn add-thing
  "Add something to the world"
  [name thing]
  (def world
    (assoc world name thing)))

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
  (add-thing "Edwin" {:type :person
                      :location [0 0]}))
