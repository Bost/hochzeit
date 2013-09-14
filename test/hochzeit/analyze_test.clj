(ns hochzeit.analyze-test
  (:use [clojure.test])
  (:require
    [clj-time.coerce :as tce]
    [hochzeit.download :as d]
    ;[hochzeit.core :as c]
    ;[clojure.java.io :as io]
    ;[clj-time.core :as tco]
    ;[clj-time.format :as tf]
    ;[me.raynes.fs :as fs]
    [liberator.util :only [parse-http-date http-date] :as du]
    [hochzeit.analyze :as a]))

(defmacro dbg [x] `(let [x# ~x] (println "analyze-test.dbg:" '~x "=" x#) x#))
(def c-save-dir d/c-save-dir)
(def c-flat-fs a/c-flat-fs)

(deftest create-fname-from-date--positive-test
  (testing "Create filename from date"
    (is (= (a/fname (a/create-date "Thu, 17 Apr 2013 21:00:00 GMT"))
           "vircurex.2013-04-17_21-00-00.xml")))
  (testing "Filepaths between from-f and to-f (alphabetical)"
    (let [path c-save-dir]
    ;; (let [path (str c-save-dir "2013/05/21")]
    (is (= (a/fpaths-between path
                                ;; from
                                (str path "vircurex.2013-05-21_04-00-04.xml")
                                ;; to
                                (str path "vircurex.2013-05-21_04-15-04.xml"))
           [(str c-save-dir "vircurex.2013-05-21_04-00-04.xml")
            (str c-save-dir "vircurex.2013-05-21_04-05-04.xml")
            (str c-save-dir "vircurex.2013-05-21_04-10-04.xml")
            (str c-save-dir "vircurex.2013-05-21_04-15-04.xml")])))))

(deftest f-between--negative-test
         (testing "Datename before zero-sized interval"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-01.xml"
                                     "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-01.xml")
                         false)))
         (testing "Datename after zero-sized interval"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-01.xml"
                                     "vircurex.2013-04-19_00-00-00.xml")
                         false)))
         (testing "Datename before non-zero-sized interval"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-01.xml"
                                     "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-03.xml")
                         false)))
         (testing "Datename after non-zero-sized interval"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-03.xml"
                                     "vircurex.2013-04-19_00-00-02.xml")
                         false))))

(deftest f-between--positive-test
         (testing "Datename in zero-sized interval (between itself)"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-00.xml")
                         true)))
         (testing "Datename between itself and an older file"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-01.xml")
                         true)))
         (testing "Datename between younger file and itself"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-01.xml"
                                     "vircurex.2013-04-19_00-00-01.xml")
                         true)))
         (testing "Datename between younger and older filef"
                  (is (= (a/between? "vircurex.2013-04-19_00-00-00.xml"
                                     "vircurex.2013-04-19_00-00-01.xml"
                                     "vircurex.2013-04-19_00-00-02.xml")
                         true))))

(deftest f-between-retval-negative-test
         (testing "Datename before zero-sized interval"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-01.xml"
                                      "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-01.xml")
                         nil)))
         (testing "Datename after zero-sized interval"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-01.xml"
                                      "vircurex.2013-04-19_00-00-00.xml")
                         nil)))
         (testing "Datename before non-zero-sized interval"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-01.xml"
                                      "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-03.xml")
                         nil)))
         (testing "Datename after non-zero-sized interval"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-03.xml"
                                      "vircurex.2013-04-19_00-00-02.xml")
                         nil))))

(deftest f-between-retval--positive-test
         (testing "Datename between itself"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-00.xml")
                         "vircurex.2013-04-19_00-00-00.xml")))
         (testing "Datename between itself and an older file"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-01.xml")
                         "vircurex.2013-04-19_00-00-00.xml")))
         (testing "Datename between younger file and itself"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-01.xml"
                                      "vircurex.2013-04-19_00-00-01.xml")
                         "vircurex.2013-04-19_00-00-01.xml")))
         (testing "Datename between younger and older filef"
                  (is (= (a/s-between "vircurex.2013-04-19_00-00-00.xml"
                                      "vircurex.2013-04-19_00-00-01.xml"
                                      "vircurex.2013-04-19_00-00-02.xml")
                         "vircurex.2013-04-19_00-00-01.xml"))))


(deftest one-f
  (testing "Just one file"
    (is (= (dbg (a/all-fpaths-between
                 c-flat-fs
                 c-save-dir
                 (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT")
                 (a/create-date "Thu, 19 Apr 2013 01:05:00 GMT")))
           ["/home/bost/vircurex-flat/vircurex.2013-04-19_01-00-03.xml"]))))

(deftest fpaths-between--positive-results
         (testing "fpaths: Zero difference between dates"
           (is (= (a/all-fpaths-between
                   c-flat-fs
                   c-save-dir
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT")
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                  [])))
         (testing "Files between 17 Apr and 18 Apr"
           (is (= (a/all-fpaths-between
                   c-flat-fs
                   c-save-dir
                   (a/create-date "Thu, 17 Apr 2013 21:00:00 GMT")
                   (a/create-date "Thu, 18 Apr 2013 00:00:00 GMT"))
                  [(str c-save-dir "vircurex.2013-04-17_21-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-20-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_21-55-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-00-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-05-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-20-05.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-35-05.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_22-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-00-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-20-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-25-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-30-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-50-03.xml")
                   (str c-save-dir "vircurex.2013-04-17_23-55-03.xml")])))
         (testing "Files between 18 Apr and 19 Apr - actually there is none."
           "TODO this may be wrong for flat dir structure."
           (is (= (a/all-fpaths-between
                   c-flat-fs
                   c-save-dir
                   (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                  [
                   (str c-save-dir "vircurex.2013-04-18_12-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-05-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-15-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-20-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-25-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-30-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-35-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_13-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-00-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-15-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-20-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-25-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-40-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-50-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_14-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-20-07.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-25-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_15-55-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-00-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-05-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-15-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-20-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-35-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_16-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-20-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-25-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-50-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_17-55-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-10-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-20-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-35-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-45-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-50-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_18-55-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-20-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_19-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-20-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_20-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-10-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-20-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-40-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_21-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-20-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-25-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-30-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-40-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_22-55-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-00-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-05-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-10-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-15-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-20-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-25-05.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-30-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-35-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-40-03.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-45-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-50-04.xml")
                   (str c-save-dir "vircurex.2013-04-18_23-55-04.xml")])))
         ;; (testing "1 hour difference between dates. Flat dir structure"
         ;;   (is (= (a/all-fpaths-between
         ;;           c-flat-fs
         ;;           c-save-dir
         ;;           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
         ;;           (a/create-date "Thu, 19 Apr 2013 01:10:00 GMT"))
         ;;          [(str c-save-dir "vircurex.2013-04-19_01-00-03.xml")
         ;;           (str c-save-dir "vircurex.2013-04-19_01-05-03.xml")])))
         ;; (testing "past-date"
         ;;   (is (= (a/all-fpaths-between
         ;;           c-flat-fs
         ;;           c-save-dir
         ;;           (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
         ;;           (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
         ;;          [(str c-save-dir "vircurex.2013-04-18_12-55-04.xml")])))
         ;; (testing "1 hour difference between dates. YYYY/MM/DD dir structure"
         ;;   (is (= (a/all-fpaths-between
                   ;; c-save-dir
         ;;           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
         ;;           (a/create-date "Thu, 19 Apr 2013 01:10:00 GMT"))
         ;;          [(str c-save-dir "2013/04/19/vircurex.2013-04-19_01-00-03.xml")
         ;;           (str c-save-dir "2013/04/19/vircurex.2013-04-19_01-05-03.xml")])))
         ;; (testing "past-date"
         ;;   (is (= (a/all-fpaths-between
                   ;; c-save-dir
         ;;           (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
         ;;           (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
         ;;          [(str c-save-dir "2013/04/18/vircurex.2013-04-18_12-55-04.xml")])))
         )

(create-fname-from-date--positive-test)
(f-between--negative-test)
(f-between--positive-test)
(f-between-retval-negative-test)
(f-between-retval--positive-test)
(fpaths-between--positive-results)

(deftest fullpath--positive-test
  (testing "Fullpath: file structure: flat"
    (is (= (a/fullpath true c-save-dir (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT"))
           c-save-dir)))
  (testing "Fullpath: file structure: YYYY/MM/DD"
    (is (= (a/fullpath false c-save-dir (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT"))
           (str c-save-dir "2013/04/18/")))))

;; TODO write a test for fpath
;; (defn fpath  [flat-fs? path date] (str (fullpath flat-fs? path date) (fname date)))

(deftest dirname--positive-test
  (testing "File structure: flat"
    (is (= (a/dirname false (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT"))
           "2013/04/18")))
  (testing "File structure: YYYY/MM/DD"
    (is (= (a/dirname true (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT"))
           ""))))

(fullpath--positive-test)
(dirname--positive-test)
