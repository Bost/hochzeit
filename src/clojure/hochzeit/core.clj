(ns hochzeit.core
  (:use [hochzeit.download :as download]
        [hochzeit.analyze :as analyze]
        )
  (:require [clj-time.format :as tf]))

(def src-uri "https://vircurex.com/api/get_info_for_currency.xml")

(def save-dir "/tmp/vircurex/")
; TODO Read System.properties to get $HOME
;(def save-dir "/home/bost/vircurex/")

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

(def result (analyze/do-analyze files :BTC :EUR :lowest-ask))
(println "result:\n" result)


(comment
;/tmp/vircurex
         )
