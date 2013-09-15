(defproject
  hochzeit "0.1.2-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.7.1"]
                 [org.clojure/math.combinatorics "0.0.4"]
                 [liberator "0.8.0"]
                 [clj-time "0.5.0"]
                 [me.raynes/fs "1.4.0"]
                 [org.clojure/clojure "1.5.1"]
                 [com.draines/postal "1.10.2"]
                 [cheshire "5.2.0"]           ; clj -> json
                 [aleph "0.3.0"]              ; websockets

                 [cc.artifice/vijual "0.2.5"] ; simple (console based) graph visualization
                 [rhizome "0.1.4"]            ; graph visualization
                 [incanter "1.5.4"]           ; statistical methods

                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/clojurescript "0.0-1878"]
                 [org.clojure/data.zip "0.1.1"]
                 [com.taoensso/timbre "1.6.0"]
                 [compojure "1.0.4"]         ; for lein-cjlsbuild
                 [hiccup "1.0.0"]            ; for lein-cjlsbuild
                 ]

  :plugins [
            [lein-cljsbuild "0.3.3-SNAPSHOT"]
            [lein-ring "0.7.0"]]

  :source-paths ["src-clj"]
  ; Enable the lein hooks for: clean, compile, test, and jar.
  ;; :hooks [leiningen.cljsbuild]
  :cljsbuild {
              :repl-listen-port 9000
              :repl-launch-commands
              ; Launch command for connecting the page of choice to the REPL.
              ; Only works if the page at URL automatically connects to the REPL,
              ; like http://localhost:3000/repl-demo does.
                                        ;     $ lein trampoline cljsbuild repl-launch firefox <URL>
              {"chromium-browser" ["chromium-browser"
                                   :stdout ".repl-chromium-out"
                                   :stderr ".repl-chromium-err"]

               "google-chrome" ["google-chrome"
                                :stdout ".repl-google-chrome-out"
                                :stderr ".repl-google-chrome-err"]

               "firefox" ["firefox"
                                :stdout ".repl-firefox-out"
                                :stderr ".repl-firefox-err"]
               }
              ;; Each entry in the :crossovers vector describes a Clojure namespace
              ;; that is meant to be used with the ClojureScript code as well.
              ;; The files that make up this namespace will be automatically copied
              ;; into the ClojureScript source path whenever they are modified.
              :crossovers [
                         example.crossover
                         hochzeit.core
                         ;; hochzeit.crossover
                         ]
              ;; ;; Set the path into which the crossover namespaces will be copied.
              ;; :crossover-path "crossover-cljs"
              ;; Set this to true to allow the :crossover-path to be copied into
              ;; the JAR file (if hooks are enabled).
              :crossover-jar true

              :builds {
                       ;; This build has the lowest level of optimizations, so it is
                       ;; useful when debugging the app.
                       :dev
                       {:source-paths ["src-cljs"]
                        :jar true
                        :compiler {:output-to "resources/public/js/main-debug.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       ;; This build has the highest level of optimizations, so it is
                       ;; efficient when running the app in production.
                       :prod
                       {:source-paths ["src-cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :advanced
                                   :pretty-print false}}
                       ;; This build is for the ClojureScript unit tests that will
                       ;; be run via PhantomJS.  See the phantom/unit-test.js file
                       ;; for details on how it's run.
                       :test
                       {:source-paths ["test-cljs"]
                        :compiler {:output-to "resources/private/js/unit-test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}}}

  :ring {:handler example.routes/app
         ;; :port 3000 ; this is the default value
         }

  :main hochzeit.core)
