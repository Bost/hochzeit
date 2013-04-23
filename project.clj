(defproject hochzeit "0.1.2-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [clj-http "0.7.1"]
                 [liberator "0.8.0"]
                 [clj-time "0.5.0"]
                 [me.raynes/fs "1.4.0"]
                 [org.clojure/clojure "1.5.1"]
                 ;[clojurewerkz/money "1.2.0"]
                 [org.clojure/data.zip "0.1.1"]
                 ]
  :source-paths      ["src/clojure"]
  :resource-paths    ["src/resources"]

  :main hochzeit.core
  )

