(ns jasons-game.draw
  (:require-macros [dommy.macros :refer [sel1]])
  (:require [ajax.core :refer [GET]]
            [clojure.string :refer [split]]
            [dommy.core :as dommy]
            [libre.sketch :as s]))

;; Styles

(def styles {:cursor {:stroke 0}
             "Edwin" {:stroke 0, :fill [0 255 0]}
             :speech-balloon {:fill [0 0 255]}
             :words {:fill 255}})

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

(defn stroke-for [name] (to-processing-color (some-> (styles name) :stroke)))
(defn fill-for [name] (to-processing-color (some-> (styles name) :fill)))

(defn set-style [name]
  (if-let [[r g b a] (stroke-for name)]
    (s/stroke r g b a)
    (s/no-stroke))
  (if-let [[r g b a] (fill-for name)]
    (s/fill r g b a)
    (s/no-fill)))


;; Text box

(defn set-text-box! [node] (dommy/replace-contents! (sel1 :#text-box) node))


;; Draw

(defn draw-it
  "Lookup and apply styles for named object, translate origin to given coordinate, and draw using supplied function"
  ([thing f] (draw-it (thing :name) (thing :location) f))
  ([name [x y] f] (s/push-style)
                  (set-style name)
                  (s/push-matrix)
                  (s/translate x y)
                  (f)  ; actually draw
                  (s/pop-matrix)
                  (s/pop-style)))

(defn shape
  [vertices]
  (s/begin-shape)
  (doseq [vertex vertices]
    (apply s/vertex vertex))
  (s/end-shape))

(def images {})

(defn load-image [id]
  (if-let [img (images id)]
    (if (symbol? img)
      ; s/load-image on the base64 image data
      (let [ext (last (split id #"\."))
            url (str "data:image/" ext ";base64," img)
            loaded-image (s/load-image url)]
        (def images (assoc images id loaded-image)))
      ; return the cached image
      img)
    
    ; not loaded yet, go get it via alax call
    (GET (str "/image/" id) {:handler (fn [response]
                                        ; can't call s/load-image here because we're not in the dynamic scope for the *processing* binding
                                        (def images (assoc images id response)))})))  ; so just store the base64 image data in the map

(defmulti draw-thing :type)
