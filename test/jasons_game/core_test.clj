(ns jasons-game.core-test
  (:require [midje.sweet :refer :all]
            [jasons-game.core :refer :all]))

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


;; Tests

(facts "about parsing text into sentence structures"
       (fact "Personal pronouns are correctly identified"
             (parse-sentence-text "I love you")
             => '{:type :sentence
                  :elements ( {:type :word, :text "I", :target :speaker}
                              {:type :word, :text "love"}
                              {:type :word, :text "you", :target :addressee})}))

(facts "about extracting text from sentence structures"
       (fact "Text can be retrieved from a sentence structure"
             (let [sentence (parse-sentence-text "I love you")]
               (text sentence))
             => "I love you"))

(facts "about environment binding"
       (fact "Target values should be replaced with values in the environment"
             (let [sentence (parse-sentence-text "I love you")]
               (bind-to-env sentence env))
             => '{:type :sentence
                  :elements ( {:type :word, :text "I", :target {:name "Jason"}}
                              {:type :word, :text "love"}
                              {:type :word, :text "you", :target {:name "Daddy"}})}))
