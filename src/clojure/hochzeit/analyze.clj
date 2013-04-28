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

; TODO change fn past-date to 24 hours
(defn past-date [date] (tco/minus date (tco/hours 24)))
(defn next-day  [date] (tco/plus date (tco/days 1)))

(def c-base-fname "vircurex")
(defn fname   [formated-date] (str c-base-fname "." formated-date ".xml"))
(defn dirname [date]          (str "" (tf/unparse c-fmt-dir date)))

(defn fname-date [date] (str "" (tf/unparse c-fmt-fname date)))

(defn dirname-between [date-from date-to]
  (let [dir-from (dirname date-from)
        dir-to   (dirname date-to)]
    (if (<= (compare dir-from dir-to) 0)
      dir-from
      nil)))

(defn create-date [s]
  (tce/from-date (du/parse-http-date s)))

(defn file-between? [fname-from tested-fname fname-to]
  (and (<= (compare fname-from tested-fname) 0)
       (<= (compare tested-fname   fname-to) 0)))

(defn file-between [fname-from tested-fname fname-to]
  (if (file-between? fname-from tested-fname fname-to)
    tested-fname
    nil))

(defn fmt-dirnames-between [date-from date-to]
  "Returns a vector of dirnames. I.e. [\"2013/04/18\" \"2013/04/19\"]"
  (let [dname-between (dirname-between date-from date-to)]
    (if (nil? dname-between)
      nil
      (into [dname-between] (fmt-dirnames-between (next-day date-from) date-to)))))

(defn fnames-between [date-from date-to]
  "Alphabetically sort files under path and return vector of full filepaths between date-from and date-to"
  (let [fname-from       (fname (fname-date date-from))
        fname-to         (fname (fname-date date-to))]
    ; TODO profile the remove nil? statement
    (into [] (first
                 ;(remove nil?
               (for [dirname-between (fmt-dirnames-between date-from date-to)]
                 (remove nil?
                         (for [f (sort (fs/list-dir (str c-save-dir dirname-between)))]
                           (file-between fname-from
                                         (str f)
                                         fname-to))))))))

(defn prepend-path [path v] (into [] (map #(str path %) v)))

(defn xfpaths-between [save-dir date-from date-to]
  (let [fb (fnames-between date-from date-to)]
    (map #(prepend-path (str save-dir % c-fsep) fb)
         (dbg (fmt-dirnames-between date-from date-to)))))

(defn fpaths-between [save-dir date-from date-to]
  ; TODO use: map #(prepend-path ...)
  (let [fb (fnames-between date-from date-to)]
    (into []
          (first
            (for [fmt-dname-between (fmt-dirnames-between date-from date-to)]
              (prepend-path (str save-dir fmt-dname-between c-fsep) fb))))))

; TODO see https://github.com/nathell/clj-tagsoup
; TODO see https://github.com/cgrand/enlive
; TODO Schejulure

