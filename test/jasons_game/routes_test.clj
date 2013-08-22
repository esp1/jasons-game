(ns jasons-game.routes-test
  (:require [clojure.test :refer [deftest]]
            [jasons-game.routes :refer :all]
            [midje.sweet :refer :all]
            [ring.mock.request :refer :all]))

(deftest routes
	(facts "about routes"
	  (fact "main route should return 200 ok"
	        (let [response (app (request :get "/"))]
	          (:status response))
	        => 200)
	
	  (fact "/invalid should return a 404 not found error"
	        (let [response (app (request :get "/invalid"))]
	          (:status response))
	        => 404)))
