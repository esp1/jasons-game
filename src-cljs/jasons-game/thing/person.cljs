(ns jasons-game.thing.person
  (:require [jasons-game.draw :as d]
            [jasons-game.thing.speech-balloon :refer [draw-speech-balloon]]
            [libre.sketch :as s]))

(def radius 50)
(def diameter (* radius 2))

(defmethod d/draw-thing :person
  [person]
  (d/draw-it person (fn []
                      (d/style {:stroke 0, :fill [0 255 0]} (fn [] (s/ellipse 0 0 diameter diameter)))
                      (draw-speech-balloon (str "My name is " (person :name)) 0 (- radius)))))

;    (s/push-matrix)
;    (s/scale 2.0)
;    (if-let [img (load-image "logo-color-255x75.png")]
;      (s/image img 100 100))
;    (s/pop-matrix)
