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
(def c-base-fname   d/c-base-fname)
(def c-flat-fs      true)
;; (def c-flat-fs     false)    ;; YYYY/MM/DD

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

(defn past-date [date] (tco/minus date (tco/hours 12)))
(defn next-day  [date] (tco/plus date (tco/days 1)))
(defn dirname   [flat-fs? date]
  "Example: (dirname false (create-date \"Thu, 18 Apr 2013 12:55:00 GMT\"))"
  "=> \"2013/04/18\""
  (if flat-fs?
    ""
    (tf/unparse c-fmt-dir date)))

(defn filename  [date] (str c-base-fname "." (tf/unparse c-fmt-fname date) ".xml"))
(defn fullpath  [flat-fs? path date] (str path (dirname flat-fs? date) (if flat-fs? "" c-fsep)))
(defn filepath  [flat-fs? path date] (str (fullpath flat-fs? path date) (filename date)))
; create-date is for debug purposes
(defn create-date [s]    (tce/from-date (du/parse-http-date s)))

(defn between? [x i y]
  (and (<= (compare x i) 0)
       (<= (compare i y) 0)))

(defn s-between [x str-i y] (if (between? x str-i y) str-i nil))

(defn str-between
  "Returns str-i if alphanumericaly str-x <= str-i <= str-y otherwise nil"
  ([str-x str-i]       (s-between str-x str-x str-i))
  ([str-x str-i str-y] (s-between str-x str-i str-y)))

(defn dirs-between [date-from date-to]
  (let [dir-between (str-between (fullpath date-from) (fullpath date-to))]
    (if (not (nil? dir-between))
      (into [dir-between] (dirs-between (next-day date-from) date-to)))))

(defn filepaths-between [path x y]
  (remove nil?
          (map #(str-between x (str path %) y)
               (into [] (sort (fs/list-dir path))))))

(defn all-filepaths-between [flat-fs? path date-from date-to]
  "TODO all-filepaths-between: implement for the YYYY/MM/DD/vircurex.*.xml file structure"
  (let [from (filepath flat-fs? path date-from)
        to   (filepath flat-fs? path date-to)]
    ;; origininally there was 'reduce into []'
    (into []
            (remove empty?
                    (filepaths-between path from to)
                    ;; (map #(filepaths-between % from to)
                    ;;      (dirs-between date-from date-to))
                    ))))

; TODO see https://github.com/nathell/clj-tagsoup
; TODO see https://github.com/cgrand/enlive
; TODO Schejulure
