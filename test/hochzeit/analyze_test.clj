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

(deftest create-filename-from-date--positive-test
  (testing "Create filename from date"
    (is (= (a/filename (a/create-date "Thu, 17 Apr 2013 21:00:00 GMT"))
           "vircurex.2013-04-17_21-00-00.xml")))
  (testing "Filepaths between from-file and to-file (alphabetical)"
    (is (= (a/filepaths-between c-save-dir
                                ;; from
                                (str c-save-dir "vircurex.2013-05-21_04-00-04.xml")
                                ;; to
                                (str c-save-dir "vircurex.2013-05-21_04-15-04.xml"))
           ;; notice the single quote char in the list below!
           '("/home/bost/vircurex-flat/vircurex.2013-05-21_04-00-04.xml"
            "/home/bost/vircurex-flat/vircurex.2013-05-21_04-05-04.xml"
            "/home/bost/vircurex-flat/vircurex.2013-05-21_04-10-04.xml"
            "/home/bost/vircurex-flat/vircurex.2013-05-21_04-15-04.xml")))))

(deftest file-between--negative-test
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

(deftest file-between--positive-test
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

(deftest file-between-retval-negative-test
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

(deftest file-between-retval--positive-test
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


(deftest fpaths-between--positive-results
         (testing "fpaths: Zero difference between dates"
           (is (= (a/all-filepaths-between
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT")
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                  [])))
         (testing "Files between 17 Apr and 18 Apr"
           (is (= (a/all-filepaths-between
                   (a/create-date "Thu, 17 Apr 2013 21:00:00 GMT")
                   (a/create-date "Thu, 18 Apr 2013 00:00:00 GMT"))
                  [])))
         (testing "Files between 18 Apr and 19 Apr - actually there is none."
           "TODO this may be wrong for flat dir structure."
           (is (= (a/all-filepaths-between
                   (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                  [])))
         (testing "1 hour difference between dates. Flat dir structure"
           (is (= (a/all-filepaths-between
                   (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
                   (a/create-date "Thu, 19 Apr 2013 01:10:00 GMT"))
                  [(str c-save-dir "vircurex.2013-04-19_01-00-03.xml")
                   (str c-save-dir "vircurex.2013-04-19_01-05-03.xml")])))
         (testing "past-date"
           (is (= (a/all-filepaths-between
                   (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                  [(str c-save-dir "vircurex.2013-04-18_12-55-04.xml")])))
         ;; (testing "1 hour difference between dates. YYYY/MM/DD dir structure"
         ;;   (is (= (a/all-filepaths-between
         ;;           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
         ;;           (a/create-date "Thu, 19 Apr 2013 01:10:00 GMT"))
         ;;          [(str c-save-dir "2013/04/19/vircurex.2013-04-19_01-00-03.xml")
         ;;           (str c-save-dir "2013/04/19/vircurex.2013-04-19_01-05-03.xml")])))
         ;; (testing "past-date"
         ;;   (is (= (a/all-filepaths-between
         ;;           (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
         ;;           (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
         ;;          [(str c-save-dir "2013/04/18/vircurex.2013-04-18_12-55-04.xml")])))
         )

(create-filename-from-date--positive-test)
(file-between--negative-test)
(file-between--positive-test)
(file-between-retval-negative-test)
(file-between-retval--positive-test)
;; (fpaths-between--positive-results)

(def c-save-dir "/home/bost/vircurex-flat-test/")

(deftest fx
         (testing "past-date"
           (is (= (a/all-filepaths-between
                   (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
                   (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                  [(str c-save-dir "vircurex.2013-04-18_12-55-04.xml")]))))

(fx)
