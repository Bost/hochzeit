(ns hochzeit.core
  (:use [hochzeit.download :as download]
        [clojure.data.zip.xml] ;s:only [attr text xml-> xml1->] 
        [hochzeit.analyze :as analyze])
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
(def extention "xml")

;(def dst-uri (download/do-download
              ;src-uri save-dir base-name fname-format extention))
;(println (str "File saved: " dst-uri))

(def directory (clojure.java.io/file (str save-dir tstamp)))
(def files (take 3 (file-seq directory)))

(def fname-base (str "./vircurex.2013-04-15_11-48-00"))
(def fname-xml (str fname-base ".xml"))
(def fname-xml (str save-dir tstamp "/vircurex.2013-04-19_05-05-04.xml"))
;(def fname-xml "/home/bost/vircurex/vircurex.2013-04-14_09-10-08.xml")
;(analyze/combine (analyze/currencies fname-xml))

;(ts/parse "http://example.com")

;(def zipped (zip/xml-zip (xml/parse fname-xml)))
;(xml1-> zipped :BTC :EUR :highest-bid text) ; (first (xml-> ...))
;(first (xml-> zipped :BTC :EUR :last-trade (attr :type)))
;(xml1-> zipped :BTC :EUR :last-trade text)

;(prn "files: " files)
;=> 
(prn "do-parse: " (analyze/do-parse files analyze/currencies))
;"do-parse: " ([:BTC :CHF :DVC :EUR :IXC :LTC :NMC :PPC :SC :TRC :USD] [:BTC :CHF :DVC :EUR :IXC :LTC :NMC :PPC :SC :TRC :USD])

; get rid of duplicates
(def hs-currencies (into #{} (reduce into (analyze/do-parse files analyze/currencies))))
;=> (prn "hs-currencies: " hs-currencies)
;"hs-currencies: " #{:PPC :BTC :USD :NMC :CHF :LTC :SC :TRC :IXC :EUR :DVC}

(def currencies (into [] hs-currencies))
;=> (prn "currencies: " currencies)
;"currencies: " [:PPC :BTC :USD :NMC :CHF :LTC :SC :TRC :IXC :EUR :DVC]

;=> (= combine create-pairs)
;true
(def combined-currencies (analyze/combine currencies))

(defn get-vals [ zpp tag-0-1 tag-2 out-type]
  "get rid of the (if ...)'s to gain speed"
  (let [v (xml1-> zpp (first tag-0-1) (second tag-0-1) tag-2 text)]
    ;(println v)
    (if (nil? v)                       
      nil
      (if (= out-type :headers)
        tag-0-1
        v))))

(defn get-zipped [fname-xml]
  (zip/xml-zip (xml/parse fname-xml)))

(def non-nil-pairs (remove nil?  (for [currency-pair combined-currencies]
                (get-vals (get-zipped fname-xml) 
                          currency-pair :highest-bid :headers))))


(defn fmt [x] (map #(format "%16s" %) x)) 

(def v (map #(get-vals zipped % :highest-bid :vals) non-nil-pairs))
(print (fmt v))
(print non-nil-pairs)



(for [zpp (analyze/do-parse files get-zipped)]
  (let [non-nil-pairs (remove nil? (for [currency-pair combined-currencies]
                                     (get-vals zpp currency-pair :highest-bid :headers)))]
    (println (fmt non-nil-pairs))
    (println (fmt
               (map #(get-vals zpp % :highest-bid :vals) non-nil-pairs)))
    ))

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

;(def first-node (ts/parse fname-xml))

;(defn val-of-children-of [node tag-name]
  ;"Get value of the child-nodes of node for given tag-name"
  ;(for [child-node (children node)
        ;:when (= (tag child-node) tag-name) ]
    ;(children child-node)))  ;(first (children child-node))) ;use it to get 'inside' of lazy-seq

;(defn key-of [node0 node1]
  ;"Create :nameNode0-nameNode1"
  ;(keyword (str (name (tag node0))
                ;"-"
                ;(name (tag node1)))))

;(defn kv-pair-of [node tag-name]
  ;"key value pairs: {:nameNode-nameChild0 tagValChild0}"
  ;"               , {:nameNode-nameChild1 tagValChild1}"
  ;"               , ..."
  ;(for [child-node (children node)]
    ;{ (key-of node child-node)
      ;(val-of-children-of child-node tag-name)}))

;(defn hash-map-of-kv-pairs [node tag-name]
  ;(into {}
        ;(for [child-node (children node)]
          ;(into {} (kv-pair-of child-node tag-name)))))

;(defn lazy-val [k hm]
  ;(first
    ;(first
      ;(k hm))))

;(def highest-bids (hash-map-of-kv-pairs first-node :highest-bid))
;;(prn highest-bids)
;;(lazy-val :BTC-EUR highest-bids)
