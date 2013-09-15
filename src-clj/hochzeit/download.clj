(ns hochzeit.download
  (:require [clj-time.coerce :as tce]
            [clj-time.format :as tf]
            [clj-http.client :as cli]
            [clojure.java.io :as io]
            ;; [liberator.util :only [parse-http-date http-date] :as du])
            [liberator.util :as du]
            ))

(defmacro dbg [x] `(let [x# ~x] (println "download.dbg:" '~x "=" x#) x#))

(def c-fsep    (System/getProperty "file.separator"))
(def c-os-name (System/getProperty "os.name"))
; TODO take a look at if-let
(def c-home-dir (if (= c-os-name "Windows 7")
                  (str "C:" c-fsep "cygwin" c-fsep "home" c-fsep
                       (System/getProperty "user.name"))
                  (System/getProperty "user.home")))

(def c-save-dir (str c-home-dir c-fsep "vircurex-flat" c-fsep))
;(def c-fsep "/") ; file.separator is not detected properly for cygwin

(def c-str-fmt-dir (str "yyyy" c-fsep "MM" c-fsep "dd"))
(def c-str-fmt-name "yyyy-MM-dd_HH-mm-ss")
(def c-base-fname "vircurex")

(def c-fmt-dir (tf/formatter c-str-fmt-dir))
(def c-fmt-fname (tf/formatter c-str-fmt-name))

(defn save-date-dir [save-dir date]
  (str save-dir (tf/unparse c-fmt-dir date) c-fsep))

; Wrap java.io.file methods
(defn exists? [f] (.exists f))
(defn is-dir? [f] (.isDirectory f))

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

(defn resp-date [http-resp]
  (tce/from-date (du/parse-http-date (:date (resp-headers http-resp)))))

(defn dst-uri! [save-dir date]
  "Create destination uri and ensure the directory structure exists. Side effects!"
  (let [s-save-date-dir (save-date-dir save-dir date)]
    (ensure-directory! s-save-date-dir)
    (str s-save-date-dir c-fsep c-base-fname "." (tf/unparse c-fmt-fname date) ".xml")))

(defn download! [src-uri save-dir]
  "Dowload file from xml and save it under given name. Side effects!"
  (let [ http-resp (cli/get src-uri
                               {:decode-body-headers true :as :auto})
        date (resp-date http-resp)
        dst-uri (dst-uri! save-dir
                          date)
        ]
    (spit dst-uri (resp-text http-resp))
    date))

(defn fix-dir-name [unfixed-dir-name]
  (if (.endsWith unfixed-dir-name c-fsep)
    unfixed-dir-name
    (str unfixed-dir-name c-fsep)))
