(ns hochzeit.core
  (:use [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->]
        ;[taoensso.timbre :as timbre :only (trace debug info warn error fatal spy)]
        [taoensso.timbre.profiling :as profiling :only (p profile)])
  (:require [hochzeit.analyze :as a]
            [hochzeit.download :as d]
            [clj-time.core :as tco]
            [clj-time.coerce :as tce]
            [clj-time.format :as tf]
            [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.java.io :as io]
            [vijual :as v]
            [clojure.pprint :as pp]
            [liberator.util :only [parse-http-date http-date] :as du])
  (:gen-class))

; TODO emails
; TODO algorithms
; TODO automatic trading

;;debugging parts of expressions
(defmacro dbg [x] `(let [x# ~x] (println "core.dbg:" '~x "=" x#) x#))

;(def c-fsep (System/getProperty "file.separator"))
(def c-fsep         d/c-fsep)
(def c-save-dir     d/c-save-dir)
(def c-str-fmt-name d/c-str-fmt-name)
(def c-fmt-fname    d/c-fmt-fname)
(def c-base-fname   d/c-base-fname)
(def c-fmt-len      (+ (.length c-str-fmt-name) 2))   ; 2-times "."

(defn full-paths [files] (map io/file files))

(defn all-currencies [save-dir date files]
  [:BTC :CHF :DVC :EUR :IXC :LTC :NMC :PPC :SC :TRC :USD])
  ;(let [cur (a/do-func a/currencies (full-paths files))]
    ;(if (empty? cur )
      ;[]
      ;(into []
            ;(into #{} ;hash-set filters out the duplicates
                  ;(reduce into cur))))))

;=> (= combine create-pairs)
;true
(defn currency-pairs [save-dir date files]
  [[:EUR :BTC]])
  ;[[:EUR :BTC] [:PPC :USD]])
  ;(a/combine (all-currencies save-dir date files)))

(defn get-vals [zpp tag-0-1 tag-2 out-type]
  "Get rid of the (if ...)'s to gain speed"
  (let [v (xml1-> zpp (first tag-0-1) (second tag-0-1) tag-2 text)]
    (if (not (nil? v))
      (if (= out-type :headers)
        tag-0-1
        v))))

(defn fmt [x] (format (str "%" c-fmt-len "s") x))

(defn get-zipped [fname-xml]
  (zip/xml-zip (xml/parse fname-xml)))

;(defn zipps [files] ;(a/do-func get-zipped (full-paths files))

;(defn currency-pair-values-for-all-tstamps [save-dir date files pairs]
  ;(for [zpp (zipps files)]
    ;(map #(get-vals zpp % :highest-bid :vals) pairs)))

(defn currency-pair-values-for-all-tstamps [save-dir date files cpairs]
  (for [zpp (a/do-func get-zipped (full-paths files))]
    (for [cp cpairs]
      (get-vals zpp cp :highest-bid :vals))))

; Wrap java.io.file methods
(defn is-file? [f] (.isFile f))
(defn get-name [f] (.getName f))
(defn get-path [f] (.getPath f))

(defn get-xml-files [file-dir pattern]
  "Recursively get a list of File objects for files ending in .xml in the given directory"
  (let [ls (if (d/is-dir? file-dir) (.listFiles file-dir))]
    (if (not (nil? ls))
      (flatten (concat
                 (filter (fn [f] (re-matches pattern (get-name f))) (filter is-file? ls))
                 (map #(get-xml-files % pattern) (filter d/is-dir? ls)))))))

(defn fname-tstamp [fname]
  "Extract timestamp form filename"
  (subs fname
        (.length (str c-base-fname "."))
        (- (.length fname) (.length ".xml"))))

(defn print-header! [download-date currency-pairs]
  (println
    (dorun
      (map #(print (fmt %))
           (into [(a/fname download-date)] currency-pairs)))))

(defn print-table! [tstamp-cp-values]
  (doseq [tstamp-values tstamp-cp-values]
    (let [tstamp (fname-tstamp (first tstamp-values))]
      (println
        (dorun (map #(print (fmt %))
                    (into [tstamp] (second tstamp-values))))))))

(defn basename [filepath]
  (.substring filepath
             (- (.length filepath) (+ (.length c-base-fname) (.length c-str-fmt-name) 2 3 ))
             (.length filepath)))

(defn analyze! [download-date save-dir-unfixed]
  "save-dir-unfixed - means add a file.separator at the end if there isn't any"
  (let [save-dir (d/fix-dir-name save-dir-unfixed)
        files-to-analyze (a/fpaths-between (a/past-date download-date)
                                           download-date)
        cur-pairs (currency-pairs save-dir download-date files-to-analyze)
        cpv-all-tstamps (currency-pair-values-for-all-tstamps save-dir
                                                              download-date
                                                              files-to-analyze
                                                              cur-pairs)
        base-file-names (map basename files-to-analyze)
        tstamp-cp-values (map vector base-file-names cpv-all-tstamps)]
    (print-header! download-date cur-pairs)
    (print-table! tstamp-cp-values)))

(defn download! [src-uri save-dir-unfixed]
  (let [save-dir (d/fix-dir-name save-dir-unfixed)]
    (d/download! src-uri save-dir)))

(def c-src-uri "http://google.com")
              ;"https://vircurex.com/api/get_info_for_currency.xml")
(def c-date (tce/from-date (du/parse-http-date
                            ;"Thu, 23 Apr 2013 22:51:00 GMT")))
                            ;"Thu, 19 Apr 2013 05:05:04 GMT")))
                            "Thu, 18 Apr 2013 00:00:00 GMT")))
                            ;"Thu, 19 Apr 2013 00:00:00 GMT")))
                            ;"Thu, 20 Apr 2013 00:00:00 GMT")))
                            ;"Thu, 15 Apr 2013 11:54:00 GMT")))
                            ;"Thu, 23 Apr 2013 10:00:04 GMT")))

(defn -main []
  ;(profile :info :Arithmetic (analyze! c-src-uri c-save-dir)))
  (analyze! c-date c-save-dir))

;(defn -main [src-uri save-dir-unfixed]
  ;(let [download-date (download! src-uri save-dir-unfixed)]
       ;;[download-date c-date]
    ;(analyze! download-date save-dir-unfixed)))

(analyze! c-date c-save-dir)

;(v/draw-tree-image [[:north-america [:usa [:miami] [:seattle] [:idaho [:boise]]]] [:europe [:germany] [:france [:paris] [:lyon] [:cannes]]]])
;(v/draw-tree [[:north-america [:usa [:miami] [:seattle] [:idaho [:boise]]]] [:europe [:germany] [:france [:paris] [:lyon] [:cannes]]]])


;(v/draw-graph-image [[:a :b] [:b :c] [:c :d] [:a :d] [:e :f] [:a :f] [:g :e] [:d :e]])
;(v/draw-graph [[:a :b] [:b :c] [:c :d] [:a :d] [:e :f] [:a :f] [:g :e] [:d :e]])
;(pp/print-table [{:a 1 :b 2 :c 3} {:b 5 :a 7 :c "dog"}])
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
