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

;(def c-src-uri "http://google.com")
(def c-src-uri "https://vircurex.com/api/get_info_for_currency.xml")

(def c-file-sep (System/getProperty "file.separator"))
(def c-os-name (System/getProperty "os.name"))
(def c-home-dir (if (= c-os-name "Windows 7")
                (str "C:" c-file-sep "cygwin" c-file-sep "home" c-file-sep
                     (System/getProperty "user.name"))
                (System/getProperty "user.home")))
(def c-save-dir (str home-dir c-file-sep "vircurex" c-file-sep))
(def c-fmt-dir (tf/formatter (str "yyyy" c-file-sep "MM" c-file-sep "dd")))
(def c-fmt-fname (tf/formatter "yyyy-MM-dd_hh-mm-ss"))
(def c-base-fname "vircurex")



(def c-date (tce/from-date (du/parse-http-date "Thu, 19 Apr 2013 05:05:04 GMT" )))
;(def date (tce/from-date (du/parse-http-date "Thu, 15 Apr 2013 11:54:00 GMT" )))
;=> (prn c-date)

(defn save-date-dir [save-dir date fmt-dir]
  (str save-dir (tf/unparse fmt-dir date)))

(defn fname-date [date fmt-fname]
  (str "" (tf/unparse fmt-fname date)))

(def c-fname-date (fname-date date fmt-fname))
;=> (prn c-save-dir)
;=> (prn c-fname-date)
;=> (prn c-fmt-dir)

(def c-save-date-dir (str (save-date-dir c-save-dir
                                         c-date
                                         c-fmt-dir) c-file-sep))
;=> (prn c-save-date-dir)
(def c-younger (a/fname-younger-than c-fname-date
                                     c-save-date-dir
                                     base-fname))
;=> (type c-younger)
;=> (prn c-younger)

(def c-vec-younger (into [] younger))
(def c-full-paths (map #(io/file (str c-save-date-dir %)) c-vec-younger))

;=>
(prn c-full-paths)

; get rid of duplicates
(def c-hs-all-currencies (into #{} (reduce into (a/do-func a/currencies c-full-paths))))
;(def c-hs-all-currencies (into #{} [:BTC :CHF :DVC :EUR :IXC :LTC :NMC :PPC :SC :TRC :USD]))
(def c-all-currencies (into [] c-hs-all-currencies))
;=>
(println "all-currencies:\n" c-all-currencies)
;[:PPC :BTC :USD :NMC :CHF :LTC :SC :TRC :IXC :EUR :DVC]

;=> (= combine create-pairs)
;true
(defn currency-pairs [] (a/combine c-all-currencies))
;(defn currency-pairs []
 ;[[:EUR :BTC] [:PPC :USD]])
;=>
(println "currency-pairs:\n" (currency-pairs))

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

(defn currency-pair-value-all-tstamps [full-paths]
  (for [zpp (a/do-func get-zipped full-paths)]
    (for [currency-pair (currency-pairs)]
      (get-vals zpp currency-pair :highest-bid :vals)
      )))

;=> (prn (currency-pair-value-all-tstamps c-full-paths))

(dorun
  (map #(print (fmt %)) (into [""] (currency-pairs))))

(doseq [currency-pair-value-tstamp (currency-pair-value-all-tstamps c-full-paths)]
  (println
    (dorun (map #(print (fmt %)) (into ["2013-04-19"] currency-pair-value-tstamp)))))

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
    (str s-save-date-dir file-sep base-fname "." (tf/unparse fmt-fname date) ".xml")))

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
(defn -main [src-uri save-dir]
  (download! src-uri save-dir fmt-dir fmt-fname base-fname))

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

