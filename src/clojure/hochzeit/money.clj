;(ns hochzeit.money)
(ns hochzeit.money
  (:require 
    [clojurewerkz.money.amounts :as ma]
    [clojurewerkz.money.currencies :as mc])
  (:import [org.joda.money.CurrencyUnit])
  )

(def BTC (clojurewerkz.money.currencies/of "BTC"))
(def LTC (clojurewerkz.money.currencies/of "LTC"))

(def EUR mc/EUR)
(def USD mc/USD)

;(* 148.52M 0.0256M)

(def no-rounding nil)
(ma/convert-to (ma/amount-of USD 200.00) EUR 0.77 no-rounding)

; TODO test against known results
; TODO check value of 1 ltc for eur
; TODO use constants for currency names

(def btc-eur { :from { :amount 1 :name BTC } :to { :amount 148.52 :name EUR} })
(def eur-btc { :from { :amount 1 :name EUR } :to { :amount 144.05 :name BTC} })
(def ltc-btc { :from { :amount 1 :name LTC } :to { :amount 0.0256 :name BTC} })

(defn buy [ amount exchange-rate ]
  (ma/convert-to 
    (ma/amount-of (:name (:from exchange-rate)) amount)
    (:name (:to exchange-rate))
    (:amount (:to exchange-rate))
    no-rounding)

  ;(* amount  
     ;(:amount (:to exchange-rate)))
  )

(defn buy-ltc-for-eur [ amount exchange-rates]
  (buy
    (buy amount (nth exchange-rates 0))
    (nth exchange-rates 1)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn s-buy [ amount exchange-rate ]
  (println (str
             "Buy " (str amount) " "
             (:name (:from exchange-rate))
             " for " (str (buy amount exchange-rate)) " "
             (:name (:to exchange-rate)) ".")))

(defn s-buy-ltc-for-eur [ amount exchange-rates ]
  (str "Buy " (str amount) " "
           (:name (:from (nth exchange-rates 0)))
           " for " 
           (str (buy-ltc-for-eur amount exchange-rates)) 
           " "
           (:name (:to (nth exchange-rates 1))) "."))

(s-buy 11 btc-eur)
;(s-buy 10 ltc-btc)
;(println (s-buy-ltc-for-eur 1 
                   ;[ltc-btc btc-eur]))


