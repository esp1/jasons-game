(ns jasons-game.thing)

(defmulti draw :type)

(defmulti bounds-in-local :type)

(defn bounds-in-parent [thing]
  (let [[x0 y0 width height] (bounds-in-local thing)
        [x y] (:location thing)]
    [(+ x x0) (+ y y0)
     width height]))

(defn contains-point
  [thing [x y]]
  (let [[x0 y0 width height] (bounds-in-parent thing)]
    (and (< x0 x (+ x0 width))
         (< y0 y (+ y0 height)))))
