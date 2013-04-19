(ns hochzeit.download
  (:require
    [clojure.xml :as xml]
    [clojure.zip :as zip]
    [clj-http.client :as client]
    [clj-time.format :as tf]
    [clj-time.coerce :as tce]
    [clj-time.core :as tco]
    [clojure.java.io :as io]
    [liberator.util :only [parse-http-date http-date] :as du]
    )
  (:gen-class)
  )

(defn ls [path]
  (let [file (java.io.File. path)]
    (if (.isDirectory file)
      (seq (.list file))
      (when (.exists file)
        [path]))))

(defn mkdir [path]
  (.mkdirs (io/file path)))

(defn ensure-directory! [path]
  (when-not (ls path)
    (mkdir path)))

(defn resp-headers [http-resp]
  (into {}
        (for [[k v] (:headers http-resp)] [(keyword k) (str v)])))

(defn resp-text [http-resp]
  (:body http-resp))

(defn resp-date-24 [date]
  (tco/minus date (tco/hours 24)))

(defn resp-date [http-resp]
  (tce/from-date (du/parse-http-date (:date (resp-headers http-resp)))))

(defn dst-uri! [save-dir fmt-dir fmt-fname base-fname date]
  "Create destination uri and ensure the directory structure exists. Side effects!"
  (let [s (str save-dir "/" (tf/unparse fmt-dir date))]
    ;(resp-date-24 date)
    (ensure-directory! s)
    (str s "/" base-fname "." (tf/unparse fmt-fname date) ".xml")))

(defn download! [src-uri save-dir fmt-dir fmt-fname base-fname]
  "Dowload file from xml and save it under given name. Side effects!"
  (let [ http-resp (client/get src-uri
                               {:decode-body-headers true :as :auto})
        dst-uri (dst-uri! save-dir
                          fmt-dir
                          fmt-fname
                          base-fname
                          (resp-date http-resp))
        ]
    (spit dst-uri (resp-text http-resp))
    dst-uri))

;(def src-uri "https://vircurex.com/api/get_info_for_currency.xml")
;(def save-dir "/tmp")
(def fmt-dir-s (tf/formatter "yyyy/MM/dd"))
(def fmt-fname-s (tf/formatter "yyyy-MM-dd_hh-mm-ss"))
(def base-fname "vircurex")

(client/get "https://vircurex.com/api/get_info_for_currency.xml" {:decode-body-headers true :as :auto})
(defn -main [src-uri save-dir]
  (download! src-uri save-dir fmt-dir-s fmt-fname-s base-fname))

