(ns jasons-game.ui.util
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :as async :refer [<! chan put!]]))

(defn ch-get
  [url]
  (let [ch (chan)]
    (GET url
         {:handler (fn [response] (put! ch response))
          :error-handler (fn [response] (throw (js/Error. response)))})
    ch))

(defn ch-post
  [url params]
  (let [ch (chan)]
    (POST url
          {:params params
           :handler (fn [response] (put! ch response))
           :error-handler (fn [response] (throw (js/Error. response)))})
    ch))
