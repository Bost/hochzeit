(ns hochzeit.core
  (:use [hochzeit.download :as download]
        [hochzeit.analyze :as analyze]
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
(def result (analyze/t (str f ".xml")))

(println "1st result:\n" (first result))
(println "2nd result:\n" (second result))
(spit (str f ".json") (first result))
