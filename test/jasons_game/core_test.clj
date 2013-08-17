(ns jasons_game.core-test
  (:require [midje.sweet :refer :all]
            [jasons_game.core :refer :all]))

;; Sentence text
;(def sentence-text "I am happy to meet you")
;(def sentence-text "I love you!")
;(def sentence-text "My name is :speaker:name")
;(def sentence-text "I am happy to meet you")

;; People
(def jason {:name "Jason"})
(def daddy {:name "Daddy"})
(def mommy {:name "Mommy"})

;; Environment
(def env {:speaker jason, :addressee daddy})

(facts "about parsing text"
       (fact "Personal pronouns are correctly identified"
             (parse-sentence-text "I love you")
             => '{:type :sentence
                  :phrases ( {:type :phrase, :words ({:type :word, :target :speaker, :text "I"})}
                             {:type :phrase, :words ({:type :word, :text "love"})}
                             {:type :phrase, :words ({:type :word, :target :addressee, :text "you"})})}))

(facts "about binding"
       (fact "Target values should be replaced with values in the environment"
             (let [sentence (parse-sentence-text "I love you")]
               (bind-to-env sentence env))
             => '{:type :sentence
                  :phrases ( {:type :phrase, :words ({:type :word, :target {:name "Jason"}, :text "I"})}
                             {:type :phrase, :words ({:type :word, :text "love"})}
                             {:type :phrase, :words ({:type :word, :target {:name "Daddy"}, :text "you"})})}))
