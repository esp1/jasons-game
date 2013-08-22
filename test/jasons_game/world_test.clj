(ns jasons-game.world-test
  (:require [jasons-game.world :refer :all]
            [midje.sweet :refer :all]))

(defn atom? [x]
  (instance? clojure.lang.Atom x))

(facts "about the world"
       (fact "a new world is an atom"
             (new-world) => atom?)
       (fact "the contents of the world is a map"
             @(new-world) => map?)
       
       (fact "adding a thing makes it appear in the world"
              (let [world (new-world)
                    thing {:name "me"}]
                (add-thing world thing)
                
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
               (get-contents world))
             => '({:name "foo"
                   :location [72 99]})))
