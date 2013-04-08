(ns hochzeit.money)

(def btc-eur 148.52)
(def eur-btc 144.05)
(def ltc-btc 0.0256)

(defn buy-for-eur [ btc-amount ]
  (* btc-amount btc-eur))

(defn buy-for-btc [ ltc-amount ]
  (* ltc-amount ltc-btc))

(defn s-buy-for-eur [ btc-amount ]
  (println "Price for " (str btc-amount) " BTC is " (str (buy-for-eur btc-amount)) " EUR."))

(defn s-buy-for-btc [ ltc-amount ]
  (println "Price for " (str ltc-amount) " LTC is " (str (buy-for-eur btc-amount)) " BTC."))

(s-buy-for-eur 10)

