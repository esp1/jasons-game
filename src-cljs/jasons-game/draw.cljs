(ns jasons-game.draw
  (:require-macros [dommy.macros :refer [sel1]])
  (:require [ajax.core :refer [GET]]
            [clojure.string :refer [split]]
            [dommy.core :as dommy]
            [libre.sketch :as s]))

;; Styles

(defn to-processing-color
  "Converts a color spec to a processing color, which is represented by a native javascript color array.
  A color spec can have any of the following forms:
  
  [ R G B A ] -> [ R G B A ]
  [ R G B ]   -> [ R G B 255 ]
  N           -> [ N N N 255 ]"
  ([] nil)
  ([color] (cond
             (coll? color) (case (count color)
                             4 (array (color 0) (color 1) (color 2) (color 3))
                             3 (array (color 0) (color 1) (color 2) 255))
             (number? color) (array color color color 255))))

(defn set-style [attrs]
  (if-let [[r g b a] (to-processing-color (:stroke attrs))]
    (s/stroke r g b a)
    (s/no-stroke))
  (if-let [[r g b a] (to-processing-color (:fill attrs))]
    (s/fill r g b a)
    (s/no-fill)))

(defn style [attrs f]
  (s/push-style)
  (set-style attrs)
  (f)
  (s/pop-style))


;; Text box

(defn set-text-box! [node] (dommy/replace-contents! (sel1 :#text-box) node))


;; Draw

(defn translate
  "Translates the origin to the given location, and draws using the supplied function.
  Optionally takes in a map of style attributes which will also be applied before drawing.
  Restores the translation matrix and styles to their previous state afterwards."
  ([location f] (translate location {} f))
  ([[x y] style-attrs f] (s/push-matrix)
                         (s/translate x y)
                         
                         (style style-attrs f)
                         
                         (s/pop-matrix)))

(defn shape
  [vertices]
  (s/begin-shape)
  (doseq [vertex vertices]
    (apply s/vertex vertex))
  (s/end-shape))

(def images {})

(defn load-image [id]
  (if-let [img (images id)]
    (cond
      (identical? :not-found img) nil
      (symbol? img) (let [url (str "data:image/png;base64," img)
                          loaded-image (s/load-image url)]  ; s/load-image on the base64 image data
                      (def images (assoc images id loaded-image))) ; return the cached image
      :else img)
    
    ; not loaded yet, go get it via alax call
    (GET (str "/image/" id ".png") {:handler (fn [response]
                                               ; can't call s/load-image here because we're not in the dynamic scope for the *processing* binding
                                               (def images (assoc images id response)))
                                    :error-handler (fn [response]
                                                     (def images (assoc images id :not-found)))})))  ; so just store the base64 image data in the map
