(ns jasons-game.ui.pjs.thing.person
  (:require [jasons-game.thing :as t]
            [jasons-game.ui.pjs.draw :as d]
            [libre.sketch :as s]))

(def radius 50)
(def diameter (* radius 2))

(defmethod t/draw :person
  [person]
  (d/translate
    (:location person)
    (fn []
      ; drawing
      (d/style
        {:stroke 0, :fill [0 255 0]}
        (fn []
          (s/ellipse 0 0 diameter diameter)))
      
      ; image
      (if-let [img (d/load-image (:name person))]
        (d/translate
          [(- (/ (.-width img) 2)) (- (/ (.-height img) 2))]
          (fn []
            (s/image img 0 0)))))))

(defmethod t/bounds-in-local :person
  [person]
  (if-let [img (d/load-image (:name person))]
    ; get bounds from image
    (let [width (.-width img)
          height (.-height img)]
      [(- (/ width 2)) (- (/ height 2))
       width height])
    
    ; get bounds from drawing
    [(- radius) (- radius)
     diameter diameter]))
