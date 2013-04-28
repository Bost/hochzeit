(ns hochzeit.analyze-test
  (:use [clojure.test])
  (:require
    [clj-time.coerce :as tce]
    ;[hochzeit.core :as c]
    ;[clojure.java.io :as io]
    ;[clj-time.core :as tco]
    ;[clj-time.format :as tf]
    ;[me.raynes.fs :as fs]
    [liberator.util :only [parse-http-date http-date] :as du]
    [hochzeit.analyze :as a]))

(defmacro dbg [x] `(let [x# ~x] (println "analyze-test.dbg:" '~x "=" x#) x#))

(deftest file-between--negative-test
         (testing "Datename before zero-sized interval"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-01.xml"
                                          "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-01.xml")
                         false)))
         (testing "Datename after zero-sized interval"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-01.xml"
                                          "vircurex.2013-04-19_00-00-00.xml")
                         false)))
         (testing "Datename before non-zero-sized interval"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-01.xml"
                                          "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-03.xml")
                         false)))
         (testing "Datename after non-zero-sized interval"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-03.xml"
                                          "vircurex.2013-04-19_00-00-02.xml")
                         false))))

(deftest file-between--positive-test
         (testing "Datename in zero-sized interval (between itself)"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-00.xml")
                         true)))
         (testing "Datename between itself and an older file"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-01.xml")
                         true)))
         (testing "Datename between younger file and itself"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-01.xml"
                                          "vircurex.2013-04-19_00-00-01.xml")
                         true)))
         (testing "Datename between younger and older filef"
                  (is (= (a/file-between? "vircurex.2013-04-19_00-00-00.xml"
                                          "vircurex.2013-04-19_00-00-01.xml"
                                          "vircurex.2013-04-19_00-00-02.xml")
                         true))))

(deftest file-between-retval-negative-test
         (testing "Datename before zero-sized interval"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-01.xml"
                                         "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-01.xml")
                         nil)))
         (testing "Datename after zero-sized interval"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-01.xml"
                                         "vircurex.2013-04-19_00-00-00.xml")
                         nil)))
         (testing "Datename before non-zero-sized interval"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-01.xml"
                                         "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-03.xml")
                         nil)))
         (testing "Datename after non-zero-sized interval"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-03.xml"
                                         "vircurex.2013-04-19_00-00-02.xml")
                         nil))))

(deftest file-between-retval--positive-test
         (testing "Datename between itself"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-00.xml")
                         "vircurex.2013-04-19_00-00-00.xml")))
         (testing "Datename between itself and an older file"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-01.xml")
                         "vircurex.2013-04-19_00-00-00.xml")))
         (testing "Datename between younger file and itself"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-01.xml"
                                         "vircurex.2013-04-19_00-00-01.xml")
                         "vircurex.2013-04-19_00-00-01.xml")))
         (testing "Datename between younger and older filef"
                  (is (= (a/file-between "vircurex.2013-04-19_00-00-00.xml"
                                         "vircurex.2013-04-19_00-00-01.xml"
                                         "vircurex.2013-04-19_00-00-02.xml")
                         "vircurex.2013-04-19_00-00-01.xml"))))

(deftest dirnames-between--positive-results
         (testing "Dirnames: Zero difference between dates"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
                           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT"))
                         ["2013/04/19"])))
         (testing "Same day, non-zero difference between dates"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
                           (a/create-date "Thu, 19 Apr 2013 03:00:00 GMT"))
                         ["2013/04/19"])))
         (testing "Different days, difference between date less that 12 hours"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
                           (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                         ["2013/04/18" "2013/04/19"])))
         (testing "Different days, difference between dates less than 24 hours"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 19 Apr 2013 23:00:00 GMT")
                           (a/create-date "Thu, 20 Apr 2013 01:00:00 GMT"))
                         ["2013/04/19" "2013/04/20"])))
         (testing "Different days, different hours, less than 48 hours difference"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
                           (a/create-date "Thu, 20 Apr 2013 03:00:00 GMT"))
                         ["2013/04/19" "2013/04/20"])))
         (testing "Exactly 48 hours difference"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
                           (a/create-date "Thu, 21 Apr 2013 01:00:00 GMT"))
                         ["2013/04/19" "2013/04/20" "2013/04/21"])))
         (testing "Different months - 1 day difference"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 30 Apr 2013 01:00:00 GMT")
                           (a/create-date "Thu, 01 May 2013 01:00:00 GMT"))
                         ["2013/04/30" "2013/05/01"])))
         (testing "Different months - several days in between"
                  (is (= (a/fmt-dirnames-between
                           (a/create-date "Thu, 29 Apr 2013 01:00:00 GMT")
                           (a/create-date "Thu, 02 May 2013 01:00:00 GMT"))
                         ["2013/04/29" "2013/04/30" "2013/05/01" "2013/05/02"]))))

(deftest fpaths-between--positive-results
         (testing "fpaths: Zero difference between dates"
                  (is (= (a/fpaths-between a/c-save-dir
                                           (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT")
                                           (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                         [])))
         (testing "17 Apr and 18 Apr"
                  (is (= (a/fpaths-between a/c-save-dir
                                           (a/create-date "Thu, 17 Apr 2013 21:00:00 GMT")
                                           (a/create-date "Thu, 18 Apr 2013 00:00:00 GMT"))
                         [])))
         (testing "1 hour difference between dates"
                  (is (= (a/fpaths-between a/c-save-dir
                                           (a/create-date "Thu, 19 Apr 2013 01:00:00 GMT")
                                           (a/create-date "Thu, 19 Apr 2013 01:10:00 GMT"))
                         ["/home/bambi/vircurex/2013/04/19/vircurex.2013-04-19_01-00-03.xml"
                          "/home/bambi/vircurex/2013/04/19/vircurex.2013-04-19_01-05-03.xml"])))
         (testing "past-date"
                  (is (= (a/fpaths-between a/c-save-dir
                                           (a/create-date "Thu, 18 Apr 2013 12:55:00 GMT")
                                           (a/create-date "Thu, 19 Apr 2013 00:00:00 GMT"))
                         ["/home/bambi/vircurex/2013/04/18/vircurex.2013-04-18_12-55-04.xml"])))
         )

(dirnames-between--positive-results)
(fpaths-between--positive-results)

