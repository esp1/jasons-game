(defproject jasons-game "0.1.0-SNAPSHOT"
  :description "A game for Jason"
  :url "http://github.com/esp1/jasons-game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/dommy "0.1.1"]]
  :profiles {:dev {:dependencies [[midje "1.6-beta1"]]}}
  :plugins [[lein-cljsbuild "0.3.2"]]
  :cljsbuild {
              :crossovers [jasons_game.core
                           jasons-game.world]
              :crossover-path "src-cljs"
              :builds [{:source-paths ["src-cljs"]
                        :compiler {:output-to "html/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
