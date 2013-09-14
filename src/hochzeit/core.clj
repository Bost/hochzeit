(ns hochzeit.core
  ;; (:use [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->]
  ;;       [clojure.pprint]
  ;;       ;[taoensso.timbre :as timbre :only (trace debug info warn error fatal spy)]
  ;;       ;[incanter.core :as ico]
  ;;       ;; [incanter.stats :as ist]
  ;;       ;[incanter.charts :as ich]
  ;;       ;[incanter.datasets :as ids]
  ;;       ;; [taoensso.timbre.profiling :as profiling :only [p profile]]
        ;; )
  ;; (:use [clojure.data.zip.xml]
  ;;       [clojure.pprint])

  (:require [hochzeit.analyze :as a]
            [hochzeit.download :as d]
            [clj-time.core :as tco]
            [clj-time.coerce :as tce]
            [clj-time.format :as tf]
            [clojure.zip :as zip]
            [clojure.xml :as xml]
            [clojure.java.io :as io]

            [clojure.contrib.math :as math]

            [clojure.data.zip.xml :as x]
            [clojure.pprint :as pp]

            [clojure.math.combinatorics :as combo]

            ;; [liberator.util :only [parse-http-date http-date] :as du])
            [liberator.util :as du]
            )
  (:gen-class)
  )

;(def plant-growth (to-matrix (ids/get-dataset :plant-growth)))
;(ist/mean (sample-normal 100))
;(sample-normal 100)
; TODO emails, algorithms,automatic trading
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
  ;; [[:EUR :BTC]]
  [[:EUR :BTC] [:BTC :EUR]]
  ;; [[:EUR :BTC] [:PPC :USD]]
  ;; [[:EUR :BTC] [:PPC :USD] [:NMC :EUR]]
  ;; (a/combine (all-currencies save-dir date files))
  )

(defn get-vals [zipped-xml-file tag-0-1 tag-2]
  "Get exchange values for a given currenty-pair (tag-0-1 = [:EUR :BTC]) from zipped-xml-file."
  "Examples: "
  "=> (get-vals (get-zipped \"/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml\") [:EUR :BTC] :highest-bid :vals)"
  "\"0.01219512\""
  "=> (get-vals (get-zipped \"/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml\") [:EUR :BTC] :highest-bid :headers)"
  "[:EUR :BTC]"
  (let [v (x/xml1-> zipped-xml-file
                  (first tag-0-1)    ; :EUR
                  (second tag-0-1)   ; :BTC
                  tag-2              ; :highest-bid
                  x/text)]             ; get textual contents (the xml1-> stuff)
    (if (not (nil? v))
      v)))

(defn get-zipped [fname-xml]
  (zip/xml-zip (xml/parse fname-xml)))

;; Wrap java.io.file methods
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

(defn fname-tstamp-raw [base-fname]
  "Extract timestamp from base-fname (= basename /path/to/base-fname)"
  (subs base-fname
        (.length (str c-base-fname "."))
        (- (.length base-fname) (.length ".xml"))))

(defn replace-in-str [working-str match]
  (clojure.string/replace working-str match ""))

(defn fname-tstamp [base-fname]
  (read-string
   (let [working-str (fname-tstamp-raw base-fname)]
     (replace-in-str (replace-in-str working-str "-") "_"))))

(defn basename [filepath]
  (.substring filepath
             (- (.length filepath) (+ (.length c-base-fname) (.length c-str-fmt-name) 2 3 ))
             (.length filepath)))

(defn download! [src-uri save-dir-unfixed]
  (let [save-dir (d/fix-dir-name save-dir-unfixed)]
    (d/download! src-uri save-dir)))

(def c-src-uri "http://google.com")
              ;"https://vircurex.com/api/get_info_for_currency.xml")

(def c-past-date (tce/from-date (du/parse-http-date
                                 "Thu, 14 Apr 2013 11:54:00 GMT")))

(def c-date (tce/from-date (du/parse-http-date
                            ;; "Fri, 06 Sep 2013 01:15:00 GMT"))
                            ;; "Sat, 06 Jul 2013 00:00:00 GMT"))
                            ;; "Thu, 06 Jun 2013 00:00:00 GMT"))
                            ;; "Mon, 05 May 2013 00:00:00 GMT"))
                            ;; "Thu, 23 Apr 2013 22:51:00 GMT"))
                            ;; "Thu, 19 Apr 2013 05:05:04 GMT"))
                            ;; "Thu, 18 Apr 2013 00:00:00 GMT"))
                            ;; "Thu, 19 Apr 2013 00:00:00 GMT"))
                            ;; "Thu, 20 Apr 2013 00:00:00 GMT"))
                            ;; "Thu, 14 Apr 2013 11:54:00 GMT"))
                            "Thu, 23 Apr 2013 10:00:04 GMT"))
  )

;(defn -main [src-uri save-dir-unfixed]
  ;(let [download-date (download! src-uri save-dir-unfixed)]
       ;;[download-date c-date]
    ;(analyze! download-date save-dir-unfixed)))


(defn plain-exchange-rates-for-file-raw [currency-pairs file-to-analyze]
  "(plain-exchange-rates-for-file [[:EUR :BTC]] \"/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml\")"
  "[\"0.01219512\" \"0.12100001\"]"
  (into []
        (for [currency-pair currency-pairs]
          (let [zipped-xml-file (get-zipped file-to-analyze)]
            (get-vals zipped-xml-file currency-pair :highest-bid)))))

(defn plain-exchange-rates-for-file [currency-pairs file-to-analyze]
  (into []
        (map #(bigdec %) (plain-exchange-rates-for-file-raw currency-pairs file-to-analyze))))

(defn exchange-rates-for-file [currency-pairs file-to-analyze]
  "(exchange-rates-for-file [[:EUR :BTC]] \"/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml\")"
  "{:[:EUR :BTC] \"0.01219512\" :[:PPC :USD] \"0.12100001\"}"
  (reduce into {}
  (let [plain-ex-rates (plain-exchange-rates-for-file currency-pairs file-to-analyze)]
    (map #(hash-map (keyword (str %1)) %2) currency-pairs plain-ex-rates))))

(defn e [currency-pairs files-to-analyze]
  "(e [[:EUR :BTC] [:PPC :USD]]"
  "                 [\"/home/bost/vircurex-flat/vircurex.2013-05-17_03-05-04.xml\""
  "                  \"/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml\"])"
  "Example:"
  "([:tstamp \"2013-05-17_03-05-04\" :[:EUR :BTC] \"0.010\"] [:[:PPC :USD] \"0.181\"]"
  " [:tstamp \"2013-05-17_03-05-04\" :[:EUR :BTC] \"0.012\"] [:[:PPC :USD] \"0.121\"]"
  " ...)"
  (for [file-to-analyze files-to-analyze]
    (let [ex-rates (plain-exchange-rates-for-file currency-pairs file-to-analyze)
          tstamp-ex-rates (map #(hash-map :value %2) currency-pairs ex-rates)
          ]
      (into { :time
             (fname-tstamp (basename file-to-analyze)) }
            tstamp-ex-rates)))

  ;; [{:time 1297110662 :value 88},
  ;;  {:time 1297110663 :value 33},
  ;;  {:time 1297110663 :value 51},
  ;;  {:time 1297110664 :value 53},
  ;;  {:time 1297110665 :value 58},
  ;;  {:time 1297110666 :value 59},]

   ;; (into [] (c/e [[:EUR :BTC] [:PPC :USD]]
   ;;               ["/home/bost/vircurex-flat/vircurex.2013-05-17_03-05-04.xml"
   ;;                "/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml"]))
)


(defn exchange-rates [currency-pairs files-to-analyze]
  "(exchange-rates [[:EUR :BTC] [:PPC :USD]]"
  "                 [\"/home/bost/vircurex-flat/vircurex.2013-05-17_03-05-04.xml\""
  "                  \"/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml\"])"
  "Example:"
  "([:tstamp \"2013-05-17_03-05-04\" :[:EUR :BTC] \"0.010\"] [:[:PPC :USD] \"0.181\"]"
  " [:tstamp \"2013-05-17_03-05-04\" :[:EUR :BTC] \"0.012\"] [:[:PPC :USD] \"0.121\"]"
  " ...)"
  (for [file-to-analyze files-to-analyze]
    (let [ex-rates (exchange-rates-for-file currency-pairs file-to-analyze)]
      (into { (keyword "tstamp")
                   (fname-tstamp (basename file-to-analyze)) }
                   ex-rates))))

(defn all-exchange-rates [save-dir download-date]
  "All exchange rates for all curreny pairs up to download-date (TODO <= or just < as download-date?)"
  "Example: see exchange-rates"
  (let [files-to-analyze (a/all-fpaths-between a/c-flat-fs
                                               c-save-dir
                                               (a/past-date download-date)
                                               ;; c-past-date
                                               download-date)]
    (exchange-rates (currency-pairs save-dir
                                    download-date
                                    files-to-analyze)
                    files-to-analyze)))

(defn analyze! [download-date save-dir-unfixed]
  "save-dir-unfixed - means add a file.separator at the end if there isn't any"
  (
   ;; count
   pp/print-table
   (all-exchange-rates (d/fix-dir-name save-dir-unfixed)
                                   download-date)))

(defn -main []
  ;(profile :info :Arithmetic (analyze! c-src-uri c-save-dir)))
  (analyze! c-date c-save-dir))

(defn count-files [past-date download-date]
  (count
   (a/all-fpaths-between a/c-flat-fs
                         c-save-dir
                         ;; (a/past-date download-date)
                         past-date
                         download-date)))

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



;; (clojure.repl/doc clojure.core/with-precision)

;; hochzeit.core> (class 1M)
;; java.math.BigDecimal
;; hochzeit.core> (class 1N)
;; clojure.lang.BigInt


;; (with-precision 3 (/ 7M 9))
;; -> 0.778M

;; (with-precision 1 (/ 7M 9))
;; -> 0.8M

;; (with-precision 1 :rounding FLOOR (/ 7M 9))
;; -> 0.7M

;; (defn round-places [number decimals]
;;   (let [factor (math/expt 10 decimals)]
;;     (bigdec (/ (math/round (* factor number)) factor))))

;; (defn round2 [x] (round-places x 2))

;; (defn x-to-y [x rate] (* x rate))

;; (defn x-to-y-rounded [x rate] (round2 (* x rate)))

;; (defn eur-to-usd [x] (x-to-y x 1.3128M))

;; (defn eur-to-usd-rounded [x] (round2 (eur-to-usd x)))
