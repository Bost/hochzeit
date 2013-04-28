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
(def c-fmt-fname    (tf/formatter c-str-fmt-name))

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

(def d (tce/from-date (du/parse-http-date "Thu, 19 Apr 2013 05:05:04 GMT" )))

; TODO change fn past-date to 24 hours
(defn past-date [date] (tco/minus date (tco/hours 1)))

(def c-base-fname "vircurex")
(defn fname [formated-date] (str c-base-fname "." formated-date ".xml"))

(defn fname-date [date] (str "" (tf/unparse c-fmt-fname date)))

(defn formated-dates-between [date-from date-to]
  ["2013/04/19"])

(defmacro dbgx [x] `(let [x# ~x]
                      ;(println "analyze.dbg:" '~x "=" x#)
                      x#))

(defn file-between? [fname-from sf fname-to]
  (and (<= (compare fname-from sf) 0)
       (<= (compare sf   fname-to) 0)))

(defn file-between [fname-from sf fname-to]
  (if (file-between? fname-from sf fname-to)
    sf
    nil))


(defn fnames-between [date-from path-from date-to path-to]
  "Alphabetically sort files under path and return fnames youger that formated-date"
  ;["/home/bambi/vircurex/2013/04/19/vircurex.2013-04-19_07-50-03.xml"
   ;"/home/bambi/vircurex/2013/04/19/vircurex.2013-04-19_09-40-05.xml"
   ;"/home/bambi/vircurex/2013/04/18/vircurex.2013-04-18_11-40-04.xml"
   ;"/home/bambi/vircurex/2013/04/19/vircurex.2013-04-19_12-50-04.xml"
   ;"/home/bambi/vircurex/2013/04/19/vircurex.2013-04-19_12-55-04.xml"])
  (let [
        formated-date-from (fname-date date-from)
        formated-date-to   (fname-date date-to)
        fname-from         (fname formated-date-from)
        fname-to           (fname formated-date-to)
        dates-between      (formated-dates-between date-from date-to)
        ]
    (dbg
      (into []
            ; TODO remove nil? could be done inside the for-loop
            (for [path-between dates-between]
                               ;["/home/bambi/vircurex/2013/04/19"]]
              (remove nil?
                      (for [f (sort (fs/list-dir (str c-save-dir path-between)))]
                        (dbg (file-between (dbg fname-from)
                                      (dbg (str f))
                                      (dbg fname-to))))))))))

(def c-from (tce/from-date (du/parse-http-date "Thu, 19 Apr 2013 00:00:00 GMT")))
(def c-to (tce/from-date (du/parse-http-date "Thu, 19 Apr 2013 00:10:00 GMT")))


(def ffrom "vircurex.2013-04-19_00-00-00.xml")
(def fsf   "vircurex.2013-04-19_01-00-03.xml")
(def fto   "vircurex.2013-04-19_02-00-00.xml")

;(file-between ffrom fsf fto)
;(fnames-between c-from "" c-to "")

(comment
  (fs/list-dir "/home/bambi/vircurex/2013/04/19")
)
; TODO see https://github.com/nathell/clj-tagsoup
; TODO see https://github.com/cgrand/enlive
; TODO Schejulure

