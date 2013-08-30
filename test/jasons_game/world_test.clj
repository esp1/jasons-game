(ns jasons-game.world-test
  (:require [clojure.test :refer [deftest]]
            [jasons-game.world :refer :all]
            [midje.sweet :refer :all]))

(deftest world
	(facts "about the world"
	       (fact "a new world is an atom"
	             (new-world) => atom?)
	       (fact "the contents of the world is a map"
	             @(new-world) => map?)
	       
	       (fact "adding a thing makes it appear in the world"
              (let [world (new-world)
                    thing {:name "me"}
                    athing (add-thing world thing)]
                (atom? athing)
                
                (count @world) => 1
                (keys @world) => '("me")
                @(@world "me") => {:name "me"}))
	       
	       (fact "removing a thing removes it from the world"
	             (let [world (new-world {:name "bleah"})]
	               (remove-thing world "bleah")
	               @world)
	             => {})
	       
	       (fact "moving a thing updates its location"
	             (let [world (new-world {:name "foo" :location [1 2]})
	                   thing (get-thing world "foo")]
	               (move-thing thing [72 99])
	               (map deref (get-contents world)))
	             => '({:name "foo"
	                   :location [72 99]}))))
