(ns jasons-game.thing.person
  (:require [jasons-game.draw :as d]
            [libre.sketch :as s]))

(def radius 50)
(def diameter (* radius 2))

(defmethod d/draw-thing :person
  [person]
  (d/draw-it person (fn []
                      (s/ellipse 0 0 diameter diameter)
                      (d/draw-speech-balloon (str "My name is " (person :name)) 0 (- radius)))))

;    (s/push-matrix)
;    (s/scale 2.0)
;    (if-let [img (load-image "logo-color-255x75.png")]
;      (s/image img 100 100))
;    (s/pop-matrix)
