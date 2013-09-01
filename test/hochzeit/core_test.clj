(ns hochzeit.core-test
  (:use [clojure.test]
        ;[hochzeit.money :as hm]
        [hochzeit.core :as c]))

(deftest a-test
  (testing "File system utilities"
    (is (= (c/basename "/path/to/files/2013/04/19/vircurex.2013-04-19_07-50-03.xml")
           "vircurex.2013-04-19_07-50-03.xml"))))


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
