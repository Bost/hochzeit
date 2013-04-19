(ns hochzeit.download-test
  (:use
    [clojure.test]
    )
  (:require
    [hochzeit.core :as c]
    [hochzeit.download :as d]
    [clojure.java.io :as io]
    ))

(def save-dir "/tmp")
(def downloaded-file (d/-main c/src-uri save-dir))

(defn file-size [fpath]
  (let [file (java.io.File. fpath)]
    (if (.isFile file)
      (.length file))))

(def downloaded-file-size (file-size downloaded-file))

(deftest file-size-test
  (testing "Size of downloaded file"
    (is (and (> downloaded-file-size 22000)
             (< downloaded-file-size 28000)))))
