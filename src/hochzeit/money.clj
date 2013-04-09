(ns hochzeit.money)

(def btc-eur { :from { :amount 1 :name "BTC" } :to { :amount 148.52 :name "EUR"} })
(def eur-btc { :from { :amount 1 :name "EUR" } :to { :amount 144.05 :name "BTC"} })
(def ltc-btc { :from { :amount 1 :name "LTC" } :to { :amount 0.0256 :name "BTC"} })

(defn buy [ amount exchange-rate ]
  (* amount  
     (:amount (:to exchange-rate))))

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
  (println (str "Buy " (str amount) " "
           (:name (:from (nth exchange-rates 0)))
           " for " 
           (str (buy-ltc-for-eur amount exchange-rates)) 
           " "
           (:name (:to (nth exchange-rates 1))) ".")))

(s-buy 10 btc-eur)
(s-buy 10 ltc-btc)
(s-buy-ltc-for-eur 1 
                   [ltc-btc btc-eur])



