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
    [liberator.util :only [parse-http-date http-date] :as du]
    ))

(def date (tce/from-date (du/parse-http-date "Sat, 19 Oct 2013 10:33:15 GMT" )))
(prn date)
(def date-24 (d/resp-date-24 date))
(prn date-24)
(tf/unparse d/fmt-dir-s date-24)
(tf/unparse d/fmt-fname-s date-24)
; Alphabetically sort files from 2013/10/19 and 2013/10/18 and take those between:
;     2013/10/18_vircurex.2013_10-33-15 
; and 
;     2013/10/19_vircurex.2013_10-33-15

  
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
