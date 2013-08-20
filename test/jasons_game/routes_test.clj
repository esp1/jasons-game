(ns jasons_game.routes-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :refer :all]
            [jasons_game.routes :refer :all]))

(facts "about routes"
  (fact "main route should return 200 ok"
        (let [response (app (request :get "/"))]
          (:status response))
        => 200)
;      (is (= (:body response) "Hello World"))))

  (fact "/invalid should return a 404 not found error"
        (let [response (app (request :get "/invalid"))]
          (:status response))
        => 404))
