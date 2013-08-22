(defproject jasons-game "0.1.0-SNAPSHOT"
  :description "A game for Jason"
  :url "http://github.com/esp1/jasons-game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.0-SNAPSHOT"]
                 [cljs-ajax "0.1.6"]
                 [compojure "1.1.5"]
                 [jayq "2.4.0"]
                 [prismatic/dommy "0.1.1"]
                 [ring-middleware-format "0.3.1"]]
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-cloverage "1.0.2"]
            [lein-ring "0.8.5"]]
  :ring {:handler jasons-game.routes/app}
  :profiles {:dev {:dependencies [[midje "1.6-beta1"]
                                  [ring-mock "0.1.5"]]}}
  :cljsbuild {:crossovers [jasons-game.core
                           jasons-game.thing
                           jasons-game.world]
              :crossover-path "crossover-cljs"
              :builds [{:source-paths ["src-cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
