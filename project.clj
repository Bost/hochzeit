(defproject hochzeit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [compojure "1.0.2"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [liberator "0.8.0"]

                 [clj-time "0.5.0"]
                 [org.apache.httpcomponents/httpcore "4.2.4"]
                 [org.apache.httpcomponents/httpclient "4.2.3"]
                 [org.apache.httpcomponents/httpmime "4.2.3"]

                 [org.clojure/clojure "1.5.1"]
                 [clojurewerkz/money "1.2.0"]
                 [clj-http "0.7.1"]
                 [org.clojure/data.json "0.2.2"]
                 [org.clojure/data.zip "0.1.1"]
                 ]
  :source-paths      ["src/clojure"]
  :resource-paths    ["src/resources"]

  :main hochzeit.download
  )

