(ns hochzeit.core
  (:use [hochzeit.download :as download]
        [hochzeit.analyze :as analyze]
        [pl.danieljanus.tagsoup :as ts]
        )
  (:require [clj-time.format :as tf]))

(def src-uri "https://vircurex.com/api/get_info_for_currency.xml")

(def os-name (System/getProperty "os.name"))
(def save-dir (if (= os-name "Windows 7")
                (str "c:\\cygwin\\home\\" (System/getProperty "user.name") "\\vircurex\\")
                (str (System/getProperty "user.home")"/vircurex/")))

;(def src-uri "http://google.com")
(def base-name "vircurex")
(def fname-format
        (tf/formatter "yyyy-MM-dd_hh-mm-ss"))
(def extention "xml")

;(def dst-uri (download/do-download
              ;src-uri save-dir base-name fname-format extention))
;(println (str "File saved: " dst-uri))


(def directory (clojure.java.io/file save-dir))
(def files (take 3
                 (file-seq directory)))

;(def result (analyze/do-analyze files :BTC :EUR :lowest-ask))
;(def result (analyze/do-parse files))
(def f (str "./vircurex.2013-04-15_11-48-00"))
;(def result (analyze/t (str f ".xml")))

;(println "1st result:\n" (first result))
;(println "2nd result:\n" (second result))
;(spit (str f ".json") (first result))

;;(ts/parse "http://example.com")
;(ts/parse src-uri)
(def p (ts/parse (str f ".xml")))
;(println p)

(def s
  (into {}
        (for [c (children p)
              :let [ctag (name (tag c))
                    cc-tag (for [cc (children c)
                                 :let [ cctag (name (tag cc)) ]]
                             {
                              (keyword (str ctag "-" cctag))
                              (for [ccc (children cc)
                                    :let [t (tag ccc)
                                          v (first (children ccc)) ]
                                    :when (= t :highest-bid) ]
                                v)
                              }
                             )
                    vcc-tag (into {} cc-tag)
                    ]
              ]
          vcc-tag
          )
        )
  )
(prn s)
(first (:BTC-EUR s))

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

