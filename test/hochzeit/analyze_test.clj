(ns hochzeit.analyze-test
  (:use
    [clojure.test]
    )
  (:require
    [hochzeit.core :as c]
    [hochzeit.download :as d]
    [hochzeit.analyze :as a]
    [clojure.java.io :as io]
    [clj-time.coerce :as tce]
    [clj-time.core :as tco]
    [clj-time.format :as tf]
    [me.raynes.fs :as fs]
    [liberator.util :only [parse-http-date http-date] :as du]
    )
  )

; TODO test for corner cases: date too high/low

(def date (tce/from-date (du/parse-http-date "Thu, 18 Apr 2013 10:33:15 GMT" )))
(prn date)
(def date-24 (d/resp-date-24 date))
(prn date-24)
(def str-date-24 (tf/unparse d/fmt-dir-s date-24))
(tf/unparse d/fmt-fname-s date-24)
(prn c/save-dir)
;(prn (d/is-dir? c/save-dir))
(def path (str c/save-dir str-date-24))

(dorun (map #(println %) (into [] (a/fname-younger-than "2013-04-17_10-55-03" path))))

;(def save-dir "/tmp")
;(def downloaded-file (d/-main c/src-uri save-dir))

;(defn file-size [fpath]
  ;(let [file (java.io.File. fpath)]
    ;(if (.isFile file)
      ;(.length file))))

;(def downloaded-file-size (file-size downloaded-file))

;(deftest file-size-test
  ;(testing "Size of downloaded file"
    ;(is (and (> downloaded-file-size 22000)
             ;(< downloaded-file-size 28000)))))
