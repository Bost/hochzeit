(ns hochzeit.analyze
  (:use 
    [clojure.xml]
    [clojure.java.io]
    [clojure.data.zip.xml ;:only (attr text xml->)
     ]
    )
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

(def os-name (System/getProperty "os.name"))
(def save-dir (if (= os-name "Windows 7")
                (str "c:\\cygwin\\home\\" (System/getProperty "user.name") "\\vircurex\\")
                (str (System/getProperty "user.home")"/vircurex/")))

(def directory (clojure.java.io/file save-dir))
(def files (file-seq directory))
(def subset-files (take 2 files))

(doseq [file subset-files]
  (let [path (.getPath file)]
    (if (not (.isDirectory file))
      ;(println path)
      (let [
            p (xml/parse path)
            zipped1 (zip/xml-zip p)]
        (println ":BTC :EUR :lowest-ask\n" 
                 (xml-> zipped1 :BTC :EUR :lowest-ask text)
                 ;" " (xml-> zipped1 :last-trade (attr :type))
                 )
        )
      )
    )
  )

(comment
  (def fname "vircurex.2013-04-15_12-55-09.xml")
  (def f (str save-dir fname))
  (def p (xml/parse f))
  (def zipped1 (zip/xml-zip p))
  (println 
    f
    (xml-> zipped1 :BTC :EUR :lowest-ask text)
    (xml-> zipped1 :last-trade (attr :type)))

  (defn zip-str [s]
    (zip/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))))

  (def paintings (zip-str "<?xml version='1.0' encoding='UTF-8'?>
                          <painting>
                          <img src='madonna.jpg' alt='Foligno Madonna, by Raphael'/>
                          <caption>This is Raphael's 'Foligno' Madonna, painted in
                          <date>1511</date>-<date>1512</date>.</caption>
                          </painting>"))

  (println 
    (xml-> paintings :caption text)
    (xml-> paintings :caption :date text)
    (xml-> paintings :img (attr :src))
    )


  )


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
