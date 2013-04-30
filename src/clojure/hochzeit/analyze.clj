(ns hochzeit.analyze
  (:use [clojure.xml]
        [clojure.java.io]
        [clojure.data.zip.xml])
  (:require [clojure.xml :as xml]
            [hochzeit.download :as d]
            [clj-time.core :as tco]
            [clojure.zip :as zip]
            [me.raynes.fs :as fs]
            [liberator.util :only [parse-http-date http-date] :as du]
            [clj-time.format :as tf]
            [clj-time.coerce :as tce])
  (:gen-class))

(def c-fsep         d/c-fsep)
(def c-save-dir     d/c-save-dir)
(def c-str-fmt-name d/c-str-fmt-name)
(def c-fmt-fname    d/c-fmt-fname)
(def c-fmt-dir      d/c-fmt-dir)
(def c-base-fname   "vircurex")

(defmacro dbg[x] `(let [x# ~x] (println "analyze.dbg:" '~x "=" x#) x#))

(defn kids [fname-xml]
  "Children below the top most tag"
  (first (:content
           (first (xml1-> (zip/xml-zip (xml/parse fname-xml)))))))

(defn currencies [fname-xml]
  (let [kids-of (kids fname-xml)]
    (into [] (cons (:tag kids-of)
                   (for [c (:content kids-of)]
                     (:tag c))))))

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

(defn past-date   [date] (tco/minus date (tco/hours 24)))
(defn next-day    [date] (tco/plus date (tco/days 1)))
(defn dirname     [date] (tf/unparse c-fmt-dir date))
(defn fname       [date] (str c-base-fname "." (tf/unparse c-fmt-fname date) ".xml"))
; create-date is for debug purposes
(defn create-date [s]    (tce/from-date (du/parse-http-date s)))

(defn between? [x i y]
  (and (<= (compare x i) 0)
       (<= (compare i y) 0)))

(defn s-between [x i y] (if (between? x i y) i nil))

(defn str-between
  ([x i]   (s-between x x i))
  ([x i y] (s-between x i y)))

(defn fmt-dirnames-between [date-from date-to]
  "Returns a vector of dirnames. I.e. [\"2013/04/18\" \"2013/04/19\"]"
  (let [dir-between (str-between (dirname date-from) (dirname date-to))]
    (if (not (nil? dir-between))
      (into [dir-between] (fmt-dirnames-between (next-day date-from) date-to)))))

(defn getpath [date-from date-to]
  (map #(str c-save-dir % c-fsep) (fmt-dirnames-between date-from date-to)))

(defn files-between [path x y]
  (remove nil?
          (map #(str-between x (str %) y) (sort (fs/list-dir path)))))

; TODO try to get rid of (into [] (first ..))
(defn fnames-between [date-from date-to paths]
  "Alphabetically sort files under path and return vector of full filepaths between date-from and date-to"
  (let [fname-from (fname date-from)
        fname-to   (fname date-to)]
    (into []
          (first
            (map #(files-between % fname-from fname-to) paths)))))

(defn prepend-path [path v] (into [] (map #(str path %) v)))

(defn fpaths-between [date-from date-to]
  (let [paths (getpath date-from date-to)
        fb (fnames-between date-from date-to paths)]
    (into []
          (first
            (map #(prepend-path % fb) paths)))))

; TODO see https://github.com/nathell/clj-tagsoup
; TODO see https://github.com/cgrand/enlive
; TODO Schejulure

