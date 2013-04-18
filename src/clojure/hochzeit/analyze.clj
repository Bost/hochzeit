(ns hochzeit.analyze
  (:use [clojure.xml]
        [clojure.java.io]
        [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->] 
        ;[clojure.data.zip.xml s:only [attr text xml-> xml1->]]
        )
  (:require [clojure.xml :as xml]
            [clj-time.format :as tf]
            [clojure.data.json :as json]
            [clojure.zip :as zip]
            [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [clj-time.core :as tcr]
            [clj-time.coerce :as tce]
            [liberator.util :only [parse-http-date http-date] :as du])
  (:gen-class))


(defn kids [fname-xml]
  "Children below the top most tag"
  (first (:content 
           (first (xml1-> (zip/xml-zip (xml/parse fname-xml)))))))

(defn currencies [fname-xml]
  (into [] (cons (:tag (kids fname-xml))
                 (for [c (:content (kids fname-xml))] 
                   (:tag c)))))

(defn combine [v]
  "Create cartesian product of vector v with vector v withouth the diagonal elements"
  (into [] (for [x v
                 y v
                 :when (not (= x y))]
             [x y])))

(defn do-parse [ files ]
  ; for builds a lazy seq; doseq is for executing side-effects and returns nil
  (for [file files]
    (let [path (.getPath file)]
      (if (not (.isDirectory file))
        (currencies path)))))

; TODO see https://github.com/nathell/clj-tagsoup
; TODO see https://github.com/cgrand/enlive
; TODO Schejulure

