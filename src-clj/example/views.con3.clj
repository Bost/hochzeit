(ns example.views
  (:require
    [example.crossover.shared :as shared]
    [hiccup
      [page :refer [html5]]
      [element :refer [javascript-tag]]
      [page :refer [include-js include-css]]]))

(defn- run-clojurescript [path init]
  (list
    (include-js path)
    (javascript-tag init)))

(defn index-page []
  (html5
    [:head
      [:title (shared/make-example-text)]
     ]
    [:body
      [:h1 (shared/make-example-text)]
      (run-clojurescript
       "/js/main-debug.js"
        "example.hello.say_hello()")]))

(defn repl-demo-page []
  (html5
    [:head
      [:title "REPL Demo"]
     (include-css "/css/style.css")
     (include-js
      "/js/renderer/three.min.js"
      "/js/renderer/tween.min.js"
      "/js/renderer/TrackballControls.js"
      "/js/renderer/CSS3DRenderer.js"
      "/js/code.js"
      )
     ]
    [:body
     {:onload "con3.cljs.main.draw(this, 'layout_layout_panel_main','layout_layout_panel_top', 'layout_layout_panel_left')"}
      "In one terminal:"
      [:pre "lein ring server-headless 3000"]
      "In a different terminal:"
      [:pre "lein trampoline cljsbuild repl-launch firefox http://localhost:3000/repl-demo"]
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

     [:div {:id "layout_layout_panel_mainX" :style "height: 960px; background-color: black;"} " ### chem-table ###"]

      (run-clojurescript
        "/js/main-debug.js"
        "example.repl.connect()")))
