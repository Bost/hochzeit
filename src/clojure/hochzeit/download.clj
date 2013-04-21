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
  (:import [java.io.File])
  (:gen-class)
  )

; Wrap java.io.file methods
(defn is-file? [f] (.isFile f))
(defn is-dir? [f] (.isDirectory f))
(defn exists? [f] (.exists f))
(defn get-name [f] (.getName f))
(defn get-path [f] (.getPath f))

(defn get-xml-files [file-dir pattern]
    "Recursively get a list of File objects for files ending in .xml in the given directory"
    (let [ls (if (is-dir? file-dir) (.listFiles file-dir) nil)
          files (filter is-file? ls)
          dirs (filter is-dir? ls)]
      (if (nil? ls)
        nil
        (flatten (concat
          (filter (fn [f] (re-matches pattern (get-name f))) files) ; .m files
          (map #(get-xml-files % pattern) dirs))))))


(defn ls-r [str-path pattern]
  (let [file-path (java.io.File. str-path)]
    (get-xml-files file-path pattern)
  ))

(defn ls [path]
  (let [file (java.io.File. path)]
    (if (is-dir? file)
      (seq (.list file))
      (when (exists? file)
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

(defn -main [src-uri save-dir]
  (download! src-uri save-dir fmt-dir-s fmt-fname-s base-fname))

;(client/get "https://vircurex.com/api/get_info_for_currency.xml" {:decode-body-headers true :as :auto})

