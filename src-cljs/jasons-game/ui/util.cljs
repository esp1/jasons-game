(ns jasons-game.ui.util
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :as async :refer [<! chan put!]]))

(defn alert-handler [response]
  (js/alert response))

(defn ch-get
  ([url] (ch-get url alert-handler))
  ([url error-handler] (let [ch (chan)]
                         (GET url
                              {:handler (fn [response] (put! ch response))
                               :error-handler error-handler})
                         ch)))

(defn ch-post
  ([url params] (ch-post url params alert-handler))
  ([url params error-handler] (let [ch (chan)]
                                (POST url
                                      {:params params
                                       :handler (fn [response] (put! ch response))
                                       :error-handler error-handler})
                                ch)))
