(ns jasons_game.core
  (:require [clojure.string :refer [split]]
            [clojure.pprint :refer [pprint]]
            [clojure.walk :refer [postwalk-replace]]))


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
  (merge {:type :word
          :text word-text}
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
  {:type :phrase
   :words (for [word-text (split-phrase-text-into-words phrase-text)]
            (create-word word-text))})
  
(defn split-sentence-text-into-elements
  [sentence-text]
  (split sentence-text #"\W"))

(defn parse-sentence-text
  "Takes in a sentence string and produces a sentence as a collection of phrases and words.
  
  The input sentence string may be annotated to indicate phrases. (TBD)
  
  The produced sentence, phrases, and words are each maps which have the following structures:

    Sentence: {:type :sentence
               :elements (...phrases and/or words...)
               ...other sentence attributes...}

    Phrase: {:type :phrase
             :words (...words...)
             ...other phrase attributes...}

    Word: {:type :word
           :text \"text\"
           ...other word attributes...}"
  [sentence-text]
  {:type :sentence,
   :elements (for [element-text (split-sentence-text-into-elements sentence-text)
                   :let [words (split element-text #"\W")]]
               (case (count words)
                 1 (create-word (first words))
                 (create-phrase words)))})


;; Environment

(defn bind-to-env
  "Binds an environment to a sentence. The environment is a map of things like :speaker and :addressee to particular
  instances of objects in the world"
  [sentence env]
  (postwalk-replace env sentence))

