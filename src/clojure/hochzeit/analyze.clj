(ns hochzeit.analyze
  (:use
    [clojure.xml]
    [clojure.java.io]
    [clojure.data.zip.xml]
    )
  (:require
    [clojure.xml :as xml]
    [clojure.zip :as zip]
    [me.raynes.fs :as fs]
    [liberator.util :only [parse-http-date http-date] :as du]
    [clj-time.coerce :as tce]
    )
  (:gen-class)
  )


(defn kids [fname-xml]
  "Children below the top most tag"
  (first (:content 
           (first (xml1-> (zip/xml-zip (xml/parse fname-xml)))))))

(defn currencies [fname-xml]
  (into [] (cons (:tag (kids fname-xml))
                 (for [c (:content (kids fname-xml))] 
                   (:tag c)))))

;(def hs-currencies (into #{} (reduce into (analyze/do-parse files analyze/currencies))))
;;=> (println "hs-currencies: " hs-currencies)
;;"hs-currencies: " #{:PPC :BTC :USD :NMC :CHF :LTC :SC :TRC :IXC :EUR :DVC}

;(def currencies (into [] hs-currencies))

(defn combine [v]
  "Create cartesian product of vector v with vector v withouth the diagonal elements"
  (into [] (for [x v
                 y v
                 :when (not (= x y))]
             [x y])))

(defn do-func [func files]
  ; for builds a lazy seq; doseq is for executing side-effects and returns nil
  (remove nil?
          (for [file files]
            (let [path (.getPath file)]
              (if (not (.isDirectory file))
                (func path))))))

(def d (tce/from-date (du/parse-http-date "Thu, 19 Apr 2013 05:05:04 GMT" )))

(defn fname-younger-than [formated-date path base-fname]
  "Alphabetically sort files under given path and return fnames youger that formated-date"
  (remove nil?
          (for [f (sort (fs/list-dir path))]
            (if (<= (compare (str base-fname "." formated-date ".xml") f) 0)
              f
              nil))))
; TODO see https://github.com/nathell/clj-tagsoup
; TODO see https://github.com/cgrand/enlive
; TODO Schejulure

