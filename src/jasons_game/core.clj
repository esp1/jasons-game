(ns jasons_game.core
  (:require [clojure.string :refer [split]]
            [clojure.pprint :refer [pprint]]
            [clojure.walk :refer []))

;; People

(def jason {:name "Jason"})
(def daddy {:name "Daddy"})
(def mommy {:name "Mommy"})

;; Sentence

(def sentence-text "I am happy to meet you")
;(def sentence-text "I love you!")
;(def sentence-text "My name is :speaker:name")
;(def sentence-text "I am happy to meet you")

;; Parts of speech

(def pronouns {:personal {"I" :speaker
                          "me" :speaker
                          "you" :addressee}
               :posessive {"my" :speaker
                           "mine" :speaker
                           "your" :addressee}})

;; Parsing
;;; Sentence is a sequence of Phrases or Words
;;; Sentence, Phrase, and Word can have metadata

(defn create-word
  "return a map with various attributes of word"
  [word-text]
  (merge {:text word-text}
         (first (filter #(not (nil? %)) (for [pronoun-type (keys pronouns)
                                              pronoun-info (pronouns pronoun-type)]
                                          (let [pronoun (first pronoun-info)
                                                pronoun-target (second pronoun-info)]
                                            (when (= (.toLowerCase pronoun) (.toLowerCase word-text))
                                              {:target pronoun-target})))))))

(defn split-phrase-text-into-words
  [phrase-text]
  [phrase-text])

(defn create-phrase
  [phrase-text]
  {:words (for [word-text (split-phrase-text-into-words phrase-text)] (create-word word-text))})
  
(defn split-sentence-text-into-phrases
  [sentence-text]
  (split sentence-text #"\W"))

(defn parse-sentence-text
  "Takes in a sentence string and produces a sentence as a collection of phrases which in turn are a collection of words.
  
  The input sentence string 
  
  The produced sentence, phrases, and words are each maps which have the following structures:
    Sentence: {:phrases [...phrases...] ...other sentence attributes...}
    Phrase: {:words [...words...] ...other phrase attributes...}
    Word: {:text \"text\" ...other word attributes...}"
  ([sentence-text] {:phrases (for [phrase-text (split-sentence-text-into-phrases sentence-text)] (create-phrase phrase-text))}))


;; Environment

(def env {:speaker jason
          :addressee daddy})

(defn bind-to-env
  [sentence env]
  )