(ns hochzeit.core
  (:use
    [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->]
    ;[taoensso.timbre :as timbre :only (trace debug info warn error fatal spy)]
    [taoensso.timbre.profiling :as profiling :only (p profile)]
    )
  (:require
    [hochzeit.analyze :as a]
    [hochzeit.download :as d]
    [clj-time.core :as tco]
    [clj-time.coerce :as tce]
    [clj-time.format :as tf]
    [clojure.zip :as zip]
    [clojure.xml :as xml]
    [clojure.java.io :as io]
    [liberator.util :only [parse-http-date http-date] :as du]
    [clojure.repl :as repl]
    )
  (:gen-class))

; TODO unit test methods
; TODO emails
; TODO algorithms
; TODO automatic trading

;;debugging parts of expressions
(defmacro dbg [x] `(let [x# ~x] (println (str *ns* ": dbg:") '~x "=" x#) x#))

;(def c-src-uri "http://google.com")
(def c-src-uri "https://vircurex.com/api/get_info_for_currency.xml")

(def c-file-sep (System/getProperty "file.separator"))
;(def c-file-sep "/") ; file.separator is not detected properly for cygwin

(def c-os-name (System/getProperty "os.name"))
(def c-home-dir (if (= c-os-name "Windows 7")
                (str "C:" c-file-sep "cygwin" c-file-sep "home" c-file-sep
                     (System/getProperty "user.name"))
                (System/getProperty "user.home")))
(def c-save-dir (str c-home-dir c-file-sep "vircurex" c-file-sep))
(def c-fmt-dir (tf/formatter (str "yyyy" c-file-sep "MM" c-file-sep "dd")))
(def c-fmt-fname (tf/formatter "yyyy-MM-dd_HH-mm-ss"))
(def c-base-fname "vircurex")


(def c-date (tce/from-date (du/parse-http-date
                            ;"Thu, 23 Apr 2013 22:51:00 GMT")))
                            ;"Thu, 19 Apr 2013 05:05:04 GMT")))
                            ;"Thu, 15 Apr 2013 11:54:00 GMT")))
                             "Thu, 22 Apr 2013 10:00:04 GMT")))

(defn fname-date [date fmt-fname]
  (str "" (tf/unparse fmt-fname date)))

(defn resp-date-24 [date]
  (tco/minus date (tco/hours 2)))

(defn full-paths [save-dir date fmt-dir fmt-fname files]
  (let [sdd (d/save-date-dir save-dir date fmt-dir c-file-sep)]
    (map #(io/file (str sdd %)) files)))

(defn all-currencies [save-dir date fmt-dir fmt-fname files]
  ;[:BTC :CHF :DVC :EUR :IXC :LTC :NMC :PPC :SC :TRC :USD])
  (let [cur (a/do-func a/currencies (full-paths save-dir date fmt-dir fmt-fname files))]
    (if (empty? cur )
      []
      (into []
            (into #{} ; use hash-set to get rid of duplicates
                  (reduce into cur))))))

;=> (= combine create-pairs)
;true
(defn currency-pairs [save-dir date fmt-dir fmt-fname files]
 ;[[:EUR :BTC] [:PPC :USD]])
  (a/combine (all-currencies save-dir date fmt-dir fmt-fname files)))

(defn get-vals [ zpp tag-0-1 tag-2 out-type]
  "get rid of the (if ...)'s to gain speed"
  (let [v (xml1-> zpp (first tag-0-1) (second tag-0-1) tag-2 text)]
    (if (nil? v)
      nil
      (if (= out-type :headers)
        tag-0-1
        v))))

(defn fmt [x length] (format (str "%" length "s") x))

(defn get-zipped [fname-xml]
  (zip/xml-zip (xml/parse fname-xml)))

(defn currency-pair-values-for-all-tstamps [save-dir date fmt-dir fmt-fname files cur-pairs]
  (for [zpp (a/do-func get-zipped (full-paths save-dir date fmt-dir fmt-fname files))]
    (for [currency-pair cur-pairs]
      (get-vals zpp currency-pair :highest-bid :vals))))

; Wrap java.io.file methods
(defn is-file? [f] (.isFile f))
(defn get-name [f] (.getName f))
(defn get-path [f] (.getPath f))

(defn get-xml-files [file-dir pattern]
    "Recursively get a list of File objects for files ending in .xml in the given directory"
    (let [ls (if (d/is-dir? file-dir) (.listFiles file-dir) nil)
          files (filter is-file? ls)
          dirs (filter d/is-dir? ls)]
      (if (nil? ls)
        nil
        (flatten (concat
          (filter (fn [f] (re-matches pattern (get-name f))) files) ; .m files
          (map #(get-xml-files % pattern) dirs))))))


(defn fname-tstamp [base-name fname]
  "Extract timestamp form filename"
  (subs fname
        (.length (str base-name "."))
        (- (.length fname) (.length ".xml"))))

(defn download-and-print-table! [src-uri save-dir-unfixed]
  "save-dir-unfixed - means add a file.separator at the end if there isn't any"
  (let [save-dir (d/fix-dir-name save-dir-unfixed c-file-sep)
        ;download-date c-date
        download-date (d/download! src-uri save-dir c-fmt-dir c-file-sep c-fmt-fname c-base-fname)
        fmt-len 20
        sdd (d/save-date-dir save-dir download-date c-fmt-dir c-file-sep)
        younger-files (into [] (a/fnames-younger-than
                                 (fname-date (resp-date-24 download-date) c-fmt-fname)
                                 sdd
                                 c-base-fname))
        cur-pairs (currency-pairs save-dir download-date c-fmt-dir c-fmt-fname younger-files)
        cpv-all-tstamps (currency-pair-values-for-all-tstamps save-dir
                                                              download-date
                                                              c-fmt-dir
                                                              c-fmt-fname
                                                              younger-files
                                                              cur-pairs)
        tstamp-cp-values (map vector younger-files cpv-all-tstamps)
        ]
    (println ; print header
             (dorun
               (map #(print (fmt % fmt-len))
                    (into [(fname-date download-date c-fmt-fname)] cur-pairs))))

    (doseq [tstamp-values tstamp-cp-values]
      (println
        (dorun (map #(print (fmt % fmt-len))
                    (into [(fname-tstamp c-base-fname
                                         (first tstamp-values))]
                          (second tstamp-values))))))))
;(defn -main []
  ;(profile :info :Arithmetic
           ;(download-print-table! "https://vircurex.com/api/get_info_for_currency.xml"
                                  ;"/home/bost/vircurex/")))

(defn download-only! [src-uri save-dir-unfixed]
  (let [save-dir (d/fix-dir-name save-dir-unfixed c-file-sep)]
    (d/download! src-uri save-dir c-fmt-dir c-file-sep c-fmt-fname c-base-fname)))

(defn -main [src-uri save-dir-unfixed]
  (download-and-print-table! src-uri save-dir-unfixed))
  ;(download-only! src-uri save-dir-unfixed))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;(
 ;([:BTC :BTC] [:BTC :AAA] [:BTC :DVC] [:BTC :EUR])
 ;([:AAA :BTC] [:AAA :AAA] [:AAA :DVC] [:AAA :EUR])
 ;([:DVC :BTC] [:DVC :AAA] [:DVC :DVC] [:DVC :EUR])
 ;([:EUR :BTC] [:EUR :AAA] [:EUR :DVC] [:EUR :EUR])
;)

;[:hash {}
 ;[:BTC {}
  ;[:AAA {}   [:lowest-ask {:type "decimal"} "0.0"]
   ;[:highest-bid {:type "decimal"} "0.0"]
   ;[:last-trade {:type "decimal"} "0.0"]
   ;[:volume {:type "decimal"} "0.0"]
   ;]
  ;[:DVC {} [:lowest-ask {:type "decimal"} "787401.57480315"]
   ;[:highest-bid {:type "decimal"} "641025.64102564"]
   ;[:last-trade {:type "decimal"} "787401.57480314"]
   ;[:volume {:type "decimal"} "13.10446095"]]
  ;[:EUR {} [:lowest-ask {:type "decimal"} "85.0"]
   ;[:highest-bid {:type "decimal"} "71.0"]
   ;[:last-trade {:type "decimal"} "75.0"]
   ;[:volume {:type "decimal"} "1.23987936"]]
  ;]
 ;[:CHF {}
  ;[:BTC {} [:lowest-ask {:type "decimal"} "0.0"]
   ;[:highest-bid {:type "decimal"} "0.0"]
   ;[:last-trade {:type "decimal"} "0.0"]
   ;[:volume {:type "decimal"} "0.0"]]
  ;[:DVC {} [:lowest-ask {:type "decimal"} "0.0"]
   ;[:highest-bid {:type "decimal"} "0.0"]
   ;[:last-trade {:type "decimal"} "0.0"]
   ;[:volume {:type "decimal"} "0.0"]
   ;]
  ;]
 ;]

