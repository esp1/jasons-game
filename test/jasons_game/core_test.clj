(ns jasons_game.core-test
  (:require [midje.sweet :refer :all]
            [jasons_game.core :refer :all]))

(facts "about parsing text"
       (fact "Parsing sentence text works"
             (parse-sentence-text "I love you")
             => '{:phrases
                  ({:words ({:target :speaker, :text "I"})}
                    {:words ({:text "love"})}
                    {:words ({:target :addressee, :text "you"})})}))
