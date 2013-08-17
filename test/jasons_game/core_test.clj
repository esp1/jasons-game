(ns jasons_game.core-test
  (:use clojure.test
        jasons_game.core))

(deftest test-parse-sentence-text
  (testing "Parse sentence text"
           (is (=
                 '{:phrases
                   ({:words ({:target :speaker, :text "I"})}
                     {:words ({:text "love"})}
                     {:words ({:target :addressee, :text "you"})})}
                 (parse-sentence-text "I love you")))))
