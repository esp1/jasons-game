(ns jasons-game.thing.person
  (:require [jasons-game.draw :as d]
            [jasons-game.thing :as t]
            [libre.sketch :as s]))

(def radius 50)
(def diameter (* radius 2))

(defmethod t/draw :person
  [person]
  (d/translate (:location person)
               {:stroke 0, :fill [0 255 0]}
               (fn []
                 (s/ellipse 0 0 diameter diameter))))

;    (s/push-matrix)
;    (s/scale 2.0)
;    (if-let [img (load-image "logo-color-255x75.png")]
;      (s/image img 100 100))
;    (s/pop-matrix)

(defmethod t/bounds-in-local :person
  [person]
  [(- radius) (- radius)
   diameter diameter])
