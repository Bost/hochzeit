(ns hochzeit.macros
  (:require [clojure.pprint :as pprint])
  (:import java.io.File))

(defmacro load-template [relative-  "Reads and returns a template as a string."
  (slurp relative-uri))
