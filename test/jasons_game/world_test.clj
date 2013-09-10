(ns jasons-game.world-test
  (:require [clojure.test :refer [deftest]]
            [jasons-game.world :refer :all]
            [midje.sweet :refer :all]))

(deftest world-test
	(facts "The world"
	       (fact "The contents of the world is a map"
	             @(new-world) => map?)
	       
	       (fact "Adding a thing makes it appear in the world"
              (let [world (new-world)]
                (count (:things @world)) => 0
                (add-thing world {:name "me"})
                (count (:things @world)) => 1
                (deref (first (:things @world))) => {:name "me"}))
	       
	       (fact "Moving a thing updates its location"
              (let [world (new-world {:name "foo" :location [1 2]})
                    thing (first (:things @world))]
                (count (:things @world)) => 1
                (modify-thing thing :location [72 99])
                (count (:things @world)) => 1
                (deref (first (:things @world)))
                => {:name "foo" :location [72 99]}))))
