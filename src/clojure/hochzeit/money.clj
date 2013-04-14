(ns hochzeit.money
  (:require 
    [clojurewerkz.money.amounts :as ma]
    [clojurewerkz.money.currencies :as mc])
  (:use [hochzeit.money :as hm])
  (:import [org.joda.money Money BigMoney CurrencyUnit MoneyUtils]
           [java.math RoundingMode BigDecimal])
  )

(def BTC (mc/of "BTC"))
(def LTC (mc/of "LTC"))

(def EUR mc/EUR)
(def USD mc/USD)

; TODO test against known results
; TODO check value of 1 ltc for eur
; TODO use constants for currency names

(def btc-eur { :from (ma/amount-of BTC 1) :to (ma/amount-of EUR 148.52) })
(def eur-btc { :from (ma/amount-of EUR 1) :to (ma/amount-of BTC 144.05) })
(def ltc-btc { :from (ma/amount-of LTC 1) :to (ma/amount-of BTC 0.0256) })

(defn buy [ buy-amount exchange-rate ]
  (let [exchange-amount (.doubleValue (.getAmount (:to exchange-rate)))
        currency-from (.getCurrencyUnit (:from exchange-rate))
        currency-to (.getCurrencyUnit (:to exchange-rate))]
    (ma/convert-to buy-amount currency-to exchange-amount :half-up)))

(defn buy-ltc-for-eur [ buy-amount exchange-rates ]
  (buy 
    ;(ma/round 
    (buy buy-amount (nth exchange-rates 0)) 
    ;(.getDecimalPlaces (nth exchange-rates 0))
    ;:half-up)
    (nth exchange-rates 1)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn s-buy-ltc-for-eur [ buy-amount exchange-rates ]
  (let [amount-to-buy (.doubleValue (.getAmount buy-amount))
        currency-from (.getCurrencyUnit (:from (nth exchange-rates 0)))
        currency-to (.getCurrencyUnit (:to (nth exchange-rates 1)))]
    (str "Buy " (str amount-to-buy) " " (str currency-from) " for " 
         (.getAmount (buy-ltc-for-eur buy-amount exchange-rates)) " "
         (str currency-to) ".")))


;(println (s-buy 10.00 hm/btc-eur))
(buy (ma/amount-of BTC 10) btc-eur)
(buy (ma/amount-of LTC 10) ltc-btc)
;(str (buy 1.00 btc-eur))
;(s-buy-ltc-for-eur 1.00 [ hm/ltc-btc hm/btc-eur ])
(s-buy-ltc-for-eur (ma/amount-of LTC 2) [ltc-btc btc-eur])

