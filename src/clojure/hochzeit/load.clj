;(ns hochzeit.load)
(ns hochzeit.load 
  ;(:use 
        ;[clojure.xml]
        ;[clojure.java.io]
        ;[clojure.data.zip.xml :only (attr text xml->)] ; dep: see below
        ;)

  (:require [clojure.xml :as xml]
            [clojure.data.json :as json]
            [clojure.zip :as zip]
            [clj-http.client :as client]
            [clj-http.cookies :as cookies]
            [clj-time.core :as tcr]
            [clj-time.format :as tf]
            [clj-time.coerce :as tce]
            [liberator.util :only [parse-http-date http-date] :as du]
            )
  (:gen-class)
  )

(defn -main [ save-dir src-uri custom-formatter ]
  (let [
        http-resp
        (client/get src-uri
                    {:decode-body-headers true :as :auto})

        resp-headers
        (into {} (for [[k v] (:headers http-resp)] [(keyword k) (str v)]))

        ; This might not be needed. I could parse the date usind clj-time
        resp-date 
        (tf/unparse custom-formatter 
                    (tce/from-date (du/parse-http-date (:date resp-headers))))

        resp-text (:body http-resp)

        dst-uri
        ;(str "/home/bost/vircurex/vircurex." resp-date ".xml")
        ;(str "vircurex." resp-date ".xml")
        (str save-dir "vircurex." resp-date ".xml")
        ]
    (spit dst-uri resp-text)
    dst-uri
    )
  )
  
;(def saveDir "/home/bost/vircurex/")
(def srcUri "https://vircurex.com/api/get_info_for_currency.xml")
;(def srcUri "http://google.com")

(def dstUri (-main saveDir srcUri))
(println (str "File saved: " dstUri))

(comment
(def src-uri "src/resources/test.json")
(def raw-content (slurp src-uri))
;(def raw-content {:cookies {:v0100session {:discard true}}})
;(println raw-content)
(def w (json/write-str raw-content))

;(clojure.pprint/pprint (:body http-resp))
;(clojure.pprint/pprint (:cookies http-resp))
;(clojure.pprint/pprint resp-headers)

(clojure.pprint/pprint (clj-http.cookies/get-cookies cs))

;(def src-uri "src/resources/data.cookies.xml")
;(def src-uri "src/resources/data.backup.xml")
;(def src-uri "src/resources/data.small.xml")

(def raw-content (slurp src-uri))
(def raw-content {:cookies {:v0100session {:discard true}}})
;(println raw-content)
(def w (json/write-str raw-content))
;(json/read-str raw-content)

(println w)

(def dst-uri "/tmp/test2.txt")
;cat "/tmp/test2.txt"
;rm /tmp/test2.txt
;/tmp/test2.txt
;ls -la "src/resources/data.small.xml"

;(def raw-content (client/get src-uri))
;(def xml (xml/parse src-uri))

(spit dst-uri raw-content)

(println "File saved: " dst-uri)

(def xml0 (xml/parse "src/resources/myfile.xml"))
(def zipped0 (zip/xml-zip xml0))
; ("Track one" "Track two")
(xml-> zipped0 :track :name text)
; ("t1" "t2")
(xml-> zipped0 :track (attr :id))      

(def zipped1 (zip/xml-zip xml))
; ("Track one" "Track two")
(xml-> zipped1 :BTC :EUR :lowest-ask text)
; ("t1" "t2")
(xml-> zipped1 :last-trade (attr :type))       

(defn zip-str [s]
  (zip/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))

(def paintings (zip-str "<?xml version='1.0' encoding='UTF-8'?>
    <painting>
      <img src='madonna.jpg' alt='Foligno Madonna, by Raphael'/>
      <caption>This is Raphael's 'Foligno' Madonna, painted in
      <date>1511</date>-<date>1512</date>.</caption>
    </painting>"))

(xml-> paintings :caption text)
(xml-> paintings :caption :date text)
(xml-> paintings :img (attr :src))

(for [
      x (xml-seq 
              (parse (java.io.File. "src/resources/data.small.xml")))
                 :when (= :lowest-ask (:tag x))
      ;y (xml-seq 
              ;(parse (java.io.File. "src/resources/data.small.xml")))
                 ;:when (= :last-trade (:tag y))
      ]
         (first (:content x))
         ;(first (:content y))
  )
(println xml)
)
