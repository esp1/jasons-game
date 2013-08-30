(ns jasons-game.core-test
  (:require [clojure.test :refer [deftest]]
            [jasons-game.core :refer :all]
            [midje.sweet :refer :all]))

(defn atom? [x]
  (instance? clojure.lang.Atom x))

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
(def env {:speaker jason, :audience daddy})


;; Tests

(deftest core
  (facts "about parsing text into sentence structures"
         (fact "personal pronouns are correctly identified"
               (parse-sentence-text "I love you")
               => '{:type :sentence
                    :elements [{:type :word, :text "I", :refers-to :speaker}
                               {:type :word, :text "love"}
                               {:type :word, :text "you", :refers-to :audience}]}))
	
	(facts "about extracting text from sentence structures"
	       (fact "text can be retrieved from a sentence structure"
              (text '{:type :sentence
                      :elements [{:type :word, :text "I", :refers-to :speaker}
                                 {:type :word, :text "love"}
                                 {:type :word, :text "you", :refers-to :audience}]})
	             => "I love you")
        (fact "sentence punctuation works"
              (text '{:type :sentence
                      :elements [{:type :word, :text "I", :refers-to :speaker}
                                 {:type :word, :text "love"}
                                 {:type :word, :text "you", :refers-to :audience}]
                      :end-punctuation \!})
              => "I love you!")
        (fact "phrase punctuation works"
              (text '{:type :sentence
                      :elements [{:type :phrase
                                  :words [{:type :word, :text "Hi"}
                                          {:type :word, :text "there"}]
                                  :end-punctuation \,}
                                 {:type :word, :text "how"}
                                 {:type :word, :text "are"}
                                 {:type :word, :text "you", :refers-to :audience}]
                      :end-punctuation \?})
              => "Hi there, how are you?"))
	
	(facts "about environment binding"
	       (fact "refers-to values should be replaced with values in the environment"
              (bind-to-env '{:type :sentence
                             :elements [{:type :word, :text "I", :refers-to :speaker}
                                        {:type :word, :text "love"}
                                        {:type :word, :text "you", :refers-to :audience}]}
                           env)
	             => '{:type :sentence
	                  :elements [{:type :word, :text "I", :refers-to {:name "Jason"}}
                              {:type :word, :text "love"}
                              {:type :word, :text "you", :refers-to {:name "Daddy"}}]})))
