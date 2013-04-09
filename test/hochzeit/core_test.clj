(ns hochzeit.core-test
  (:use clojure.test
        ;hochzeit.core
        hochzeit.money
        ))

(deftest a-test
  (testing "Vector of two currencies"
    (is (=
           (s-buy-ltc-for-eur 1 [ltc-btc btc-eur])
           "Buy 1 LTC for 3.8021120000000006 EUR."
           ))))
