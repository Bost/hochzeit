(ns hochzeit.core-test
  (:use [clojure.test]
        ;[hochzeit.money :as hm]
        [hochzeit.core :as c]))

(deftest a-test
  (testing "File system utilities"
    (is (= (basename "/path/to/files/2013/04/19/vircurex.2013-04-19_07-50-03.xml")
           "vircurex.2013-04-19_07-50-03.xml")))

  (testing "Testing functions for the fn time-value-pairs"
    (is (= (plain-x-rates-for-file-raw [[:EUR :BTC] [:PPC :USD]]
                                       "/home/bost/vircurex-flat/vircurex.2013-05-17_03-05-04.xml")
           ["0.01054073" "0.18"]))
    (is (= (plain-x-rates-for-file-raw [[:EUR :BTC] [:PPC :USD]]
                                       "/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml")
           ["0.01219512" "0.12100001"]))

    (is (= (time-value-pairs [[:EUR :BTC]]
                             ["/home/bost/vircurex-flat/vircurex.2013-05-17_03-05-04.xml"
                              "/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml"
                              "/home/bost/vircurex-flat/vircurex.2013-05-25_17-10-03.xml"])
           '({:time 20130517030504 :value 0.01054073M}
             {:time 20130414115026 :value 0.01219512M}
             {:time 20130525171003 :value 0.00944465M}))))

  (testing "fname-tstamp-raw"
    (is (= (fname-tstamp-raw "vircurex.2013-05-17_03-05-04.xml")
           "2013-05-17_03-05-04")))

  (testing "fname-tstamp"
    (is (= (fname-tstamp "vircurex.2013-05-17_03-05-04.xml")
           20130517030504))))



;(deftest a-test
  ;(testing "Vector of two currencies"
    ;(is (=
         ;(s-buy-ltc-for-eur 1 [hm/ltc-btc hm/btc-eur])
         ;"Buy 1 LTC for 3.80 EUR."
         ;))
    ;(is (=
         ;(s-buy 10.00 hm/btc-eur)
         ;"Buy 10.0 BTC for 1485.20 EUR."
         ;))
    ;(is (=
         ;(s-buy 10.00 hm/ltc-btc)
         ;"Buy 10.0 LTC for 0.256000000 BTC."
         ;))

    ;))
(a-test)
