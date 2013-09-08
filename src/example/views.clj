(ns example.views
  (:require
   ;; [hochzeit.core :as c]
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js]]]))

(defn index-page []
  (html5
    [:head
     [:title "Hello World"]
     (include-js "/js/main.js")
     ;; (include-js "http://d3js.org/d3.v3.min.js")  ; TODO "utf-8" ?
     ;; <script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
     ]
    [:body
      [:h1 "Hello World"]
     ;; (c/eur-to-usd 100)
     ]))
