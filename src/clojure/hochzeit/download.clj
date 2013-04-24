(ns hochzeit.download
  (:use
    [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->]
    )
  (:require
    [hochzeit.core :as c]
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

; TODO separate stuff from namespace core to download
; TODO unit test methods
; TODO emails
; TODO algorithms
; TODO automatic trading

;;debugging parts of expressions
(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))

(defn save-date-dir [save-dir date fmt-dir]
  (str save-dir (tf/unparse fmt-dir date) c/c-file-sep))

(defn fname-date [date fmt-fname]
  (str "" (tf/unparse fmt-fname date)))

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
  (tco/minus date (tco/hours 1)))

(defn resp-date [http-resp]
  (tce/from-date (du/parse-http-date (:date (resp-headers http-resp)))))

(defn dst-uri! [save-dir fmt-dir fmt-fname base-fname date]
  "Create destination uri and ensure the directory structure exists. Side effects!"
  (let [s-save-date-dir (save-date-dir save-dir date fmt-dir)]
    ;(resp-date-24 date)
    (ensure-directory! s-save-date-dir)
    (str s-save-date-dir c/c-file-sep base-fname "." (tf/unparse fmt-fname date) ".xml")))

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
    date
    ))

(defn fname-tstamp [base-name fname]
  "Extract timestamp form filename"
  (subs fname
        (.length (str base-name "."))
        (- (.length fname) (.length ".xml"))))

(defn fix-dir-name [unfixed-dir-name]
  (if (.endsWith unfixed-dir-name c/c-file-sep)
    unfixed-dir-name
    (str unfixed-dir-name c/c-file-sep)))


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

