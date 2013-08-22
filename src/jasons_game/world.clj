(ns jasons-game.world)

;; Things

(defn get-thing
  [world name]
  (@world name))

(defn add-thing
  "Adds a thing to the world. The thing will be wrapped in an atom."
  [world thing]
  (swap! world assoc (thing :name) (atom thing)))

(defn remove-thing
  "Removes a thing from the world"
  [world name]
  (swap! world dissoc name))

(defn move-thing
  "Changes the location of a thing"
  [thing location]
  (swap! thing assoc :location location))


;; World
; The world is an ordered map of objects.
; The order of the objects indicates their z-ordering when they are drawn.
(defn new-world
  ([& things] (let [world (atom (sorted-map))]
                (doseq [thing things]
                  (add-thing world thing))
                world)))

(defn get-contents
  [world]
  (for [[k v] @world]
    (deref v)))


;; Population

(defn populate-world [world]
  (doto world
	  (add-thing {:type :person
	              :name "Edwin"
	              :location [400 200]})
	  (add-thing {:type :person
	              :name "Christine"
	              :location [700 300]})))
