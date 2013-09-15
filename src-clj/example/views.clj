(ns example.views
  (:require
   ;; [hochzeit.core :as c]
    [hiccup
      [page :refer [html5]]
      [element :refer [javascript-tag]]
      [page :refer [include-js include-css]]]))

(defn- run-clojurescript [path init]
  (let [ret (list
             (include-js path)
             (javascript-tag init))]
    ;; printl goes to stdout opened for 'lein ring server-headless 3000'
    (println "--Executed: run-clojurescript" path init)
    ;; (println "ret: " ret)
    ret))

(defn index-page []
  (html5
   [:head
    [:title "Hochzeit"]
    (include-css "/css/bar.css")
    ;; (include-js "/js/box.js")
    ;; (include-js "/js/box-plot.js")
    ]
   [:body
    (include-js "/js/main.js")
    (include-js "/js/d3.v3.min.js")
   ;; [:h1 "Hello World"]
   ;; (c/eur-to-usd 100)
   ]
   ))

(defn repl-demo-page []
  (html5
    [:head
      [:title "REPL Demo XX"]
     ;; (include-css "/css/style.css")
     ;; (include-js "/js/main.js") ; this is probably not needed
     ]
    [:body
     ;; {:onload "con3.cljs.main.draw(this, 'layout_layout_panel_main','layout_layout_panel_top', 'layout_layout_panel_left')"}
      "In one terminal:"
      [:pre "lein ring server-headless 3000"]
      "In a different terminal:"
      [:pre "lein trampoline cljsbuild repl-launch google-chrome http://localhost:3000/repl-demo"]
      "Alternately, you can run:"
      [:pre "lein ring server-headless 3000 &
lein trampoline cljsbuild repl-listen"]]
      [:h2 {:id "fun"} "REPL commands:"]
      [:pre " (js/alert \"Hello!\")
 (load-namespace 'goog.date.Date)
 (js/alert (goog.date.Date.))
 (.log js/console (reduce + [1 2 3 4 5]))
 (load-namespace 'goog.dom)
 (goog.dom.setTextContent (goog.dom.getElement \"fun\") \"I changed something....\") "]

      (run-clojurescript
        "/js/main-debug.js"
        ;; "/js/d3.v3.js"
        "example.repl.connect()")))
