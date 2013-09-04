(ns hochzeit.core
  (:use [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->]
        [clojure.pprint]
        ;[taoensso.timbre :as timbre :only (trace debug info warn error fatal spy)]
        ;[incanter.core :as ico]
        [incanter.stats :as ist]
        ;[incanter.charts :as ich]
        ;[incanter.datasets :as ids]
        [taoensso.timbre.profiling :as profiling :only [p profile]])
  (:require [hochzeit.analyze :as a]
            [hochzeit.download :as d]
            [clj-time.core :as tco]
            [clj-time.coerce :as tce]
            [clj-time.format :as tf]
            [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.math.combinatorics :as combo]
            [liberator.util :only [parse-http-date http-date] :as du])
  (:gen-class))

;(def plant-growth (to-matrix (ids/get-dataset :plant-growth)))
;(ist/mean (sample-normal 100))
;(sample-normal 100)

; TODO emails
; TODO algorithms
; TODO automatic trading
; TODO Learn clojure https://github.com/relevance/labrepl.git
; TODO search for patterns of code for which there might exist a more idiomatic function or macro https://github.com/jonase/kibit.git
;
; TODO graphs: Gremlin is a graph traversal language: java: https://github.com/tinkerpop/gremlin
;                                                     clojure: https://github.com/olabini/clj-gremlin.git
;              Visualization: https://github.com/ztellman/rhizome (Starred 516 times, requires graphviz installed: dot -V)
;                             https://github.com/drcode/vijual.git (Starred 67 times, can produce textmode graphs, from Conrad Banski)
; TODO JVM Metrics: https://github.com/codahale/metrics

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
  "Return currency pais a la [[:EUR :BTC] [:PPC :USD]]"
  ;; [[:EUR :BTC]])
  [[:EUR :BTC] [:PPC :USD]])
  ;; [[:EUR :BTC] [:PPC :USD] [:NMC :EUR]])
  ;; (a/combine (all-currencies save-dir date files)))

(defn get-vals [zpp tag-0-1 tag-2 out-type]
  "Get rid of the (if ...)'s to gain speed"
  (let [v (xml1-> zpp (first tag-0-1) (second tag-0-1) tag-2 text)]
    (if (not (nil? v))
      (if (= out-type :headers)
        tag-0-1
        v))))

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


(defn basename [filepath]
  (.substring filepath
             (- (.length filepath) (+ (.length c-base-fname) (.length c-str-fmt-name) 2 3 ))
             (.length filepath)))

(defn analyze! [download-date save-dir-unfixed]
  "save-dir-unfixed - means add a file.separator at the end if there isn't any"
  (let [save-dir (d/fix-dir-name save-dir-unfixed)
        files-to-analyze (a/all-filepaths-between
                          a/c-flat-fs
                          c-save-dir
                          (a/past-date download-date)
                          download-date)
        cur-pairs (currency-pairs save-dir download-date files-to-analyze)
        kv (map #(keyword (str %)) (into [] cur-pairs))
        cp-vals (into [] (currency-pair-values-for-all-tstamps
                          save-dir
                          download-date
                          files-to-analyze
                          cur-pairs))
        base-file-names (map basename files-to-analyze)
        tstamps (into [] (map #(fname-tstamp %) base-file-names))
        ]
    ;; pretty print table
    (print-table
     (dbg (vector (keyword "tstamp") kv))
     ;; [
     ;;  { :tstamp 2013-04-14_11-50-26, :[:EUR :BTC] 0.01219512, :[:PPC :USD] 0.12100001 }
     ;;  { :tstamp 2013-04-14_11-50-27, :[:EUR :BTC] 0.01219512, :[:PPC :USD] 0.12100001 }
     ;;  { :tstamp 2013-04-14_11-51-39, :[:EUR :BTC] 0.01219512, :[:PPC :USD] 0.12100001 }
     ;;  { :tstamp 2013-04-14_11-51-40, :[:EUR :BTC] 0.01219512, :[:PPC :USD] 0.12100001 }
     ;;]
     ;; (for [t tstamps   k kv  cp cp-vals] [t  k  (nth cp (.indexOf k))])

     ;; (def pval (into [] (for [k kv  cp cp-vals] [k (nth cp (.indexOf kv k))])))
     ;; (def half-pval (range 1 (+ 1 (/ (count pval) 2))))
     ;; (def combs (into [] (map #(concat (nth pval (- % 1))  (nth pval (- (* 2 %) 1)) ) half-pval)))
     ;; (def vcombs (into [] (map #(into [] %) combs)))
     ;; (def almost (into [] (map #(into [] (concat %1 [:tstamp %2])) vcombs tstamps)))
     (dbg
     (into []
           ;; (map #(hash-map
           ;;      (nth kv 0) %1
           ;;      (nth kv 1) (nth %2 0)
           ;;      (nth kv 2) (nth %2 1))
           ;;      (dbg tstamps)
           ;;      (dbg cp-vals)
           (for [t tstamps
                 k kv
                 cp cp-vals]
             (hash-map (keyword "tstamp") (str t)  k  (nth cp (.indexOf kv k))))
           )))
     ))

(defn download! [src-uri save-dir-unfixed]
  (let [save-dir (d/fix-dir-name save-dir-unfixed)]
    (d/download! src-uri save-dir)))

(def c-src-uri "http://google.com")
              ;"https://vircurex.com/api/get_info_for_currency.xml")
(def c-date (tce/from-date (du/parse-http-date
                            ;"Thu, 23 Apr 2013 22:51:00 GMT")))
                            ;"Thu, 19 Apr 2013 05:05:04 GMT")))
                            ;; "Thu, 18 Apr 2013 00:00:00 GMT")))
                            ;"Thu, 19 Apr 2013 00:00:00 GMT")))
                            ;"Thu, 20 Apr 2013 00:00:00 GMT")))
                            "Thu, 14 Apr 2013 11:54:00 GMT")))
                            ;"Thu, 23 Apr 2013 10:00:04 GMT")))

(defn -main []
  ;(profile :info :Arithmetic (analyze! c-src-uri c-save-dir)))
  (analyze! c-date c-save-dir))

;(defn -main [src-uri save-dir-unfixed]
  ;(let [download-date (download! src-uri save-dir-unfixed)]
       ;;[download-date c-date]
    ;(analyze! download-date save-dir-unfixed)))

;(analyze! c-date c-save-dir)

(defn cpv [download-date save-dir-unfixed]
  "Currency-Pair-Values; save-dir-unfixed - means add a file.separator at the end if there isn't any"
  (let [save-dir (d/fix-dir-name save-dir-unfixed)
        files-to-analyze (a/all-filepaths-between
                          a/c-flat-fs
                          c-save-dir
                          (a/past-date download-date)
                          download-date)
        cur-pairs (currency-pairs save-dir download-date files-to-analyze)
        cpv-all-tstamps (currency-pair-values-for-all-tstamps save-dir
                                                              download-date
                                                              files-to-analyze
                                                              cur-pairs)]
        cpv-all-tstamps))


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
