(ns hochzeit.download
  (:require
    [clj-time.coerce :as tce]
    [clj-time.format :as tf]
    [clj-http.client :as cli]
    [clojure.java.io :as io]
    [liberator.util :only [parse-http-date http-date] :as du]
    ))

;;debugging parts of expressions
(defmacro dbg [x] `(let [x# ~x] (println (str *ns* ": dbg:") '~x "=" x#) x#))

(defn save-date-dir [save-dir date fmt-dir file-sep]
  (str save-dir (tf/unparse fmt-dir date) file-sep))

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

(defn dst-uri! [save-dir fmt-dir fmt-fname file-sep base-fname date]
  "Create destination uri and ensure the directory structure exists. Side effects!"
  (let [s-save-date-dir (save-date-dir save-dir date fmt-dir file-sep)]
    (ensure-directory! s-save-date-dir)
    (str s-save-date-dir file-sep base-fname "." (tf/unparse fmt-fname date) ".xml")))

(defn download! [src-uri save-dir fmt-dir file-sep fmt-fname base-fname]
  "Dowload file from xml and save it under given name. Side effects!"
  (let [ http-resp (cli/get src-uri
                               {:decode-body-headers true :as :auto})
        date (resp-date http-resp)
        dst-uri (dst-uri! save-dir
                          fmt-dir
                          fmt-fname
                          file-sep
                          base-fname
                          date)
        ]
    (spit dst-uri (resp-text http-resp))
    date))

(defn fix-dir-name [unfixed-dir-name file-sep]
  (if (.endsWith unfixed-dir-name file-sep)
    unfixed-dir-name
    (str unfixed-dir-name file-sep)))
