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

                 [cc.artifice/vijual "0.2.5"] ; simple (console based) graph visualization
                 [rhizome "0.1.4"]            ; graph visualization
                 [incanter "1.5.4"]           ; statistical methods

                 [org.clojure/clojure-contrib "1.2.0"]

                 [org.clojure/data.zip "0.1.1"]
                 [com.taoensso/timbre "1.6.0"]
                 [compojure "1.0.4"]         ; for lein-cjlsbuild
                 [hiccup "1.0.0"]            ; for lein-cjlsbuild
                 ]

  :plugins [[lein-cljsbuild "0.3.3-SNAPSHOT"]
            [lein-ring "0.7.0"]]

  :source-paths ["src"]                       ; clj sources

  :cljsbuild {
              ;; Each entry in the :crossovers vector describes a Clojure namespace
              ;; that is meant to be used with the ClojureScript code as well.
              ;; The files that make up this namespace will be automatically copied
              ;; into the ClojureScript source path whenever they are modified.
              :crossovers [
                           hochzeit.core
                           hochzeit.macros
                           hochzeit.crossover
                           ]
              ;; ;; Set the path into which the crossover namespaces will be copied.
              :crossover-path "crossover-cljs"
              ;; Set this to true to allow the :crossover-path to be copied into
              ;; the JAR file (if hooks are enabled).
              :crossover-jar false

              :builds [{:source-paths ["src-cljs"
                                       "crossover-cljs"
                                       ]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :ring {:handler example.routes/app}

  :main hochzeit.core)
