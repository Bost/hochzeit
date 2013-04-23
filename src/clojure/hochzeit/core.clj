(ns hochzeit.core
  (:use
    [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->]
    )
  (:require
    [hochzeit.analyze :as a]
    [clj-time.core :as tco]
    [clj-time.coerce :as tce]
    [clj-time.format :as tf]
    [clojure.zip :as zip]
    [clojure.xml :as xml]
    [clj-http.client :as client]
    [clojure.java.io :as io]
    [liberator.util :only [parse-http-date http-date] :as du]
    )
  (:gen-class)
  )

; TODO integrate download & analysis to the -main method
; TODO separate stuff from namespace core to download
; TODO method younger-than should get a parameter time-interval
; TODO unit test methods
; TODO emails
; TODO algorithms
; TODO automatic trading

;;debugging parts of expressions
(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))

;(def c-src-uri "http://google.com")
(def c-src-uri "https://vircurex.com/api/get_info_for_currency.xml")

(def c-file-sep (System/getProperty "file.separator"))
(def c-os-name (System/getProperty "os.name"))
(def c-home-dir (if (= c-os-name "Windows 7")
                (str "C:" c-file-sep "cygwin" c-file-sep "home" c-file-sep
                     (System/getProperty "user.name"))
                (System/getProperty "user.home")))
(def c-save-dir (str c-home-dir c-file-sep "vircurex" c-file-sep))
(def c-fmt-dir (tf/formatter (str "yyyy" c-file-sep "MM" c-file-sep "dd")))
(def c-fmt-fname (tf/formatter "yyyy-MM-dd_hh-mm-ss"))
(def c-base-fname "vircurex")



(def c-date (tce/from-date (du/parse-http-date "Thu, 22 Apr 2013 11:56:00 GMT" )))
;(def c-date (tce/from-date (du/parse-http-date "Thu, 19 Apr 2013 05:05:04 GMT" )))
;(def date (tce/from-date (du/parse-http-date "Thu, 15 Apr 2013 11:54:00 GMT" )))

(defn save-date-dir [save-dir date fmt-dir]
  (str save-dir (tf/unparse fmt-dir date) c-file-sep))

(defn fname-date [date fmt-fname]
  (str "" (tf/unparse fmt-fname date)))

(defn full-paths [save-dir date fmt-dir fmt-fname]
  (let [sdd (save-date-dir save-dir date fmt-dir)]
    (map #(io/file (str sdd %))
         (into []
               (a/fname-younger-than (fname-date date fmt-fname)
                                     sdd
                                     c-base-fname)))))

(defn all-currencies [save-dir date fmt-dir fmt-fname]
  (into []
        (into #{} ; use hash-set to get rid of duplicates
              ;[:BTC :CHF :DVC :EUR :IXC :LTC :NMC :PPC :SC :TRC :USD]))
              (reduce into (a/do-func a/currencies (dbg (full-paths save-dir date fmt-dir fmt-fname)))))))

;=> (= combine create-pairs)
;true
(defn currency-pairs [save-dir date fmt-dir fmt-fname]
 ;([[:EUR :BTC] [:PPC :USD]]))
  (a/combine (all-currencies save-dir date fmt-dir fmt-fname)))

(defn get-vals [ zpp tag-0-1 tag-2 out-type]
  "get rid of the (if ...)'s to gain speed"
  (let [v (xml1-> zpp (first tag-0-1) (second tag-0-1) tag-2 text)]
    (if (nil? v)
      nil
      (if (= out-type :headers)
        tag-0-1
        v))))

(defn fmt [x] (format "%16s" x))

(defn get-zipped [fname-xml]
  (zip/xml-zip (xml/parse fname-xml)))

(defn currency-pair-value-all-tstamps [save-dir date fmt-dir fmt-fname]
  (for [zpp (a/do-func get-zipped (full-paths save-dir date fmt-dir fmt-fname))]
    (for [currency-pair (currency-pairs save-dir date fmt-dir fmt-fname)]
      (get-vals zpp currency-pair :highest-bid :vals))))

(defn -main []
(dorun
  (map #(print (fmt %)) (into [""] (currency-pairs c-save-dir c-date c-fmt-dir c-fmt-fname))))

(doseq [cp-val-tstamp (currency-pair-value-all-tstamps c-save-dir c-date c-fmt-dir c-fmt-fname)]
  (println
    (dorun (map #(print (fmt %)) (into ["2013-04-19"] cp-val-tstamp)))))
  )

; Wrap java.io.file methods
(defn is-file? [f] (.isFile f))
(defn is-dir? [f] (.isDirectory f))
(defn exists? [f] (.exists f))
(defn get-name [f] (.getName f))
(defn get-path [f] (.getPath f))

(defn get-xml-files [file-dir pattern]
    "Recursively get a list of File objects for files ending in .xml in the given directory"
    (let [ls (if (is-dir? file-dir) (.listFiles file-dir) nil)
          files (filter is-file? ls)
          dirs (filter is-dir? ls)]
      (if (nil? ls)
        nil
        (flatten (concat
          (filter (fn [f] (re-matches pattern (get-name f))) files) ; .m files
          (map #(get-xml-files % pattern) dirs))))))


(defn ls-r [str-path pattern]
  (let [file-path (java.io.File. str-path)]
    (get-xml-files file-path pattern)
  ))

(defn ls [path]
  (let [file (java.io.File. path)]
    (if (is-dir? file)
      (seq (.list file))
      (when (exists? file)
        [path]))))

(defn mkdir [path]
  (.mkdirs (io/file path)))

(defn ensure-directory! [path]
  (when-not (ls path)
    (mkdir path)))

(defn resp-headers [http-resp]
  (into {}
        (for [[k v] (:headers http-resp)] [(keyword k) (str v)])))

(defn resp-text [http-resp]
  (:body http-resp))

(defn resp-date-24 [date]
  (tco/minus date (tco/hours 24)))

(defn resp-date [http-resp]
  (tce/from-date (du/parse-http-date (:date (resp-headers http-resp)))))

(defn dst-uri! [save-dir fmt-dir fmt-fname base-fname date]
  "Create destination uri and ensure the directory structure exists. Side effects!"
  (let [s-save-date-dir (save-date-dir save-dir date fmt-dir)]
    ;(resp-date-24 date)
    (ensure-directory! s-save-date-dir)
    (str s-save-date-dir c-file-sep base-fname "." (tf/unparse fmt-fname date) ".xml")))

(defn download! [src-uri save-dir fmt-dir fmt-fname base-fname]
  "Dowload file from xml and save it under given name. Side effects!"
  (let [ http-resp (client/get src-uri
                               {:decode-body-headers true :as :auto})
        date (resp-date http-resp)
        dst-uri (dst-uri! save-dir
                          fmt-dir
                          fmt-fname
                          base-fname
                          date)
        ]
    (spit dst-uri (resp-text http-resp))
    (a/fname-younger-than date
                          (save-date-dir save-dir date fmt-dir)
                          base-fname)
    ;dst-uri
    ))
(defn -mainx [src-uri save-dir]
  (download! src-uri save-dir c-fmt-dir c-fmt-fname c-base-fname))

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

