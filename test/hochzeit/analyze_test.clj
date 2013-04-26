(ns hochzeit.analyze-test
  (:use [clojure.test])
  (:require
    [hochzeit.analyze :as a]
    ;[hochzeit.core :as c]
    ;[clojure.java.io :as io]
    ;[clj-time.coerce :as tce]
    ;[clj-time.core :as tco]
    ;[clj-time.format :as tf]
    ;[me.raynes.fs :as fs]
    ;[liberator.util :only [parse-http-date http-date] :as du]
    ))


(def ffrom "vircurex.2013-04-19_00-00-00.xml")
(def fsf   "vircurex.2013-04-19_01-00-03.xml")
(def fto   "vircurex.2013-04-19_02-00-00.xml")

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
         (testing "Datename between itself"
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

