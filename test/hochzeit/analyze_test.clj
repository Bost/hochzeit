(ns hochzeit.analyze-test
  (:use
    [clojure.test]
    )
  (:require
    [hochzeit.core :as c]
    [hochzeit.analyze :as a]
    [clojure.java.io :as io]
    [clj-time.coerce :as tce]
    [clj-time.core :as tco]
    [clj-time.format :as tf]
    [me.raynes.fs :as fs]
    [liberator.util :only [parse-http-date http-date] :as du]
    )
  )

(def downloaded-files (c/-main c/c-src-uri c/c-save-dir))
(prn "downloaded-files: " downloaded-files)

(defn file-size [fpath]
  (let [file (java.io.File. fpath)]
    (if (.isFile file)
      (.length file))))

;(def downloaded-file-size (file-size (first downloaded-files)))

;(deftest file-size-test
  ;(testing "Size of downloaded file"
    ;(is (and (> downloaded-file-size 22000)
             ;(< downloaded-file-size 28000)))))

; TODO test for corner cases: date too high/low

(def date (tce/from-date (du/parse-http-date "Thu, 18 Apr 2013 10:33:15 GMT" )))
(def date-24 (c/resp-date-24 date))
(prn date-24)
(def str-date-24 (tf/unparse c/fmt-dir date-24))
(tf/unparse c/fmt-fname date-24)
(prn c/save-dir)
;(prn (c/is-dir? c/save-dir))
(def path (str c/save-dir str-date-24))
(prn (c/save-date-dir c/save-dir date c/fmt-dir))
(dorun (map #(println %) (into [] (a/fname-younger-than "2013-04-17_10-55-03" path c/base-fname))))
