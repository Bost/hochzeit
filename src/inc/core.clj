(ns inc.core
  (:use [incanter core stats charts]))

(defn foo
  ""
  [x]
  ;; (view (histogram (sample-normal 1000)))
  ;; (view (function-plot sin -10 10))
  (view (function-plot sin -4 4))
  )
