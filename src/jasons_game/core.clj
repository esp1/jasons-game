(ns jasons-game.core
  (:require [clojure.string :refer [join lower-case split]]
            [clojure.walk :refer [postwalk-replace]]
            [clojure.zip :as zip]))

;; Parts of speech

(def pronouns {:personal {"I" :speaker
                          "me" :speaker
                          "you" :audience}
               :posessive {"my" :speaker
                           "mine" :speaker
                           "your" :audience}})


;; Parsing text into sentence structures

(defn split-phrase-text-into-words
  "Splits a phrase into a collection of individual words"
  [phrase-text]
  (split phrase-text #"\W"))
  
(defn split-sentence-text-into-elements
  "Splits a sentence text string into a collection of individual phrases and words"
  [sentence-text]
  (split sentence-text #"\W"))

(defn create-word
  "return a map with various attributes of word"
  [word-text]
  (merge {:type :word
          :text word-text}
         (first (filter #(not (nil? %)) (for [pronoun-type (keys pronouns)
                                              pronoun-info (pronouns pronoun-type)]
                                          (let [pronoun (first pronoun-info)
                                                pronoun-target (second pronoun-info)]
                                            (when (= (lower-case pronoun) (lower-case word-text))
                                              {:refers-to pronoun-target})))))))

(defn create-phrase
  [phrase-text]
  {:type :phrase
   :words (for [word-text (split-phrase-text-into-words phrase-text)]
            (create-word word-text))})

(defn parse-sentence-text
  "Takes in a sentence string and produces a sentence as a collection of phrases and words.
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

(defn sentence-zipper
  [sentence]
  (letfn
    [(get-children
       [node]
       (condp :type node
         :sentence (:elements node)
         :phrase (:words node)))]
    (zip/zipper get-children
                get-children
                (fn [node children]
                  (condp :type node
                    :sentence (assoc node :elements children)
                    :phrase (assoc node :words children)))
                sentence)))

(defn word? [loc] (= (:type (zip/node loc)) :word))

(defn apply-style
  [loc style]
  (if (word? loc)
    (zip/edit loc assoc :style style)
    loc))

(defn style-highlight [loc] (apply-style loc :highlight))
(defn style-normal [loc] (apply-style loc :normal))

;; Extracting text from sentence structures

(defmulti text
  "Extracts the text of a sentence, phrase, or word"
  :type)
(defmethod text :sentence [sentence] (str (when-let [p (:start-punctuation sentence)] p)
                                          (join " " (map text (:elements sentence)))
                                          (when-let [p (:end-punctuation sentence)] p)))
(defmethod text :phrase [phrase] (str (when-let [p (:start-punctuation phrase)] p)
                                      (join " " (map text (:words phrase)))
                                      (when-let [p (:end-punctuation phrase)] p)))
(defmethod text :word [word] (str (when-let [p (:start-punctuation word)] p)
                                  (:text word)
                                  (when-let [p (:end-punctuation word)] p)))


;; Environment

(defn bind-to-env
  "Binds an environment to a sentence. The environment is a map of things like :speaker and :audience to particular
  instances of objects in the world"
  [sentence env]
  (postwalk-replace env sentence))

(defn resolve-aliases [sentence env]
  (reduce
    (fn [s [key val]] (clojure.string/replace s (str ":" (name key)) (:name val)))
    sentence
    env))
