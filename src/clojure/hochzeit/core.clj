(ns hochzeit.core
  (:use
    [hochzeit.download :as download]
    [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->]
    [hochzeit.analyze :as analyze]
    )
  (:require
    [clj-time.format :as tf]
    [clojure.zip :as zip]
    [clojure.xml :as xml]
    ))

(def src-uri "https://vircurex.com/api/get_info_for_currency.xml")

(def os-name (System/getProperty "os.name"))
(def save-dir (if (= os-name "Windows 7")
                (str "c:\\cygwin\\home\\" (System/getProperty "user.name") "\\vircurex\\")
                (str (System/getProperty "user.home")"/vircurex/")))

;(def src-uri "http://google.com")
(def base-name "vircurex")
(def tstamp "2013/04/19")
(def fname-format (tf/formatter "yyyy-MM-dd_hh-mm-ss"))

(def directory (clojure.java.io/file (str save-dir tstamp)))
(def files (take 7 (file-seq directory)))
;=> (println "files:\n" files)

(def fname-base (str "./vircurex.2013-04-15_11-48-00"))
(def fname-xml (str fname-base ".xml"))
(def fname-xml (str save-dir tstamp "/vircurex.2013-04-19_05-05-04.xml"))
;=> (analyze/currencies fname-xml)
;[:BTC :CHF :DVC :EUR :IXC :LTC :NMC :PPC :SC :TRC :USD]

; get rid of duplicates
(def hs-all-currencies (into #{} (reduce into (analyze/do-func analyze/currencies files))))
(def all-currencies (into [] hs-all-currencies))
;=> (println "all-currencies:\n" all-currencies)
;[:PPC :BTC :USD :NMC :CHF :LTC :SC :TRC :IXC :EUR :DVC]

;=> (= combine create-pairs)
;true
(defn currency-pairs [] (analyze/combine all-currencies))
;(defn currency-pairs []
 ;[[:EUR :BTC] [:PPC :USD]])
;=> (println "currency-pairs:\n" (currency-pairs))

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

(defn currency-pair-value-all-tstamps []
  (for [zpp (analyze/do-func get-zipped files)]
    (for [currency-pair (currency-pairs)]
      (get-vals zpp currency-pair :highest-bid :vals)
      )))

;(currency-pair-value-all-tstamps)
(comment
(dorun
  (map #(print (fmt %)) (into [""] (currency-pairs))))

(doseq [currency-pair-value-tstamp (currency-pair-value-all-tstamps)]
  (println
    (dorun (map #(print (fmt %)) (into ["2013-04-19"] currency-pair-value-tstamp)))))
)
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

