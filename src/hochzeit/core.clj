(ns hochzeit.core)

(def given-items 
  (hash-map
    :vases         (hash-map :price 135.0 :name "Vasen")
    :flowers       (hash-map :price 15.0  :name "Blumen")
    :greeting-card (hash-map :price 2.75  :name "Hochszeitskarte")
    :cash          (hash-map :price 100.0 :name "Bargeld")))

(def persons
  (hash-map :bost         (hash-map :name "Bost"        
                                    :given-cash 0.0
                                    :fixed-cash 0.0
                                    :bought-items '( :vases
                                                   :flowers
                                                   :greeting-card))
            :michael      (hash-map :name "Michael"     
                                    :given-cash 50.0
                                    :fixed-cash 0.0
                                    :bought-items '())
            :andreas      (hash-map :name "Andreas"     
                                    :given-cash 50.0
                                    :fixed-cash 0.0
                                    :bought-item'())
            :rosa-carsten (hash-map :name "RosaCarsten" 
                                    :given-cash 0.0
                                    :fixed-cash 20.0
                                    :bought-items '())))

(defn list-of [ a-key name-def-hash-map ] 
  (map #(a-key %) (vals name-def-hash-map)))

(println "list-of names:"       (list-of :name persons))
(println "list-of given-cash"   (list-of :given-cash persons))
(println "list-of fixed-cash"   (list-of :fixed-cash persons))
; TODO how to concatenate lists?
(println "list-of bought-items" (concat (list-of :bought-items persons)))
(println "list-of given-items"  (list-of :name given-items))
         

(defn total [ cash-key name-def-hash-map ] 
  (reduce + (list-of cash-key name-def-hash-map)))

(def total-price-given-items (total :price given-items))

(println "total-price-given-items" total-price-given-items)

(def total-given-cash (total :given-cash persons))
(println "total given-cash:" total-given-cash)

(def total-fixed-cash (total :fixed-cash persons))
(println "total fixed-cash:" total-fixed-cash)

(def cash-to-divide (- total-given-cash total-fixed-cash))
(println "cash-to-divide:" cash-to-divide)

(def cnt-persons-divisible-cash
  (count (filter zero? (list-of :fixed-cash persons))))

(println "cnt-persons-divisible-cash:" cnt-persons-divisible-cash)

(defn cash-to-give-by [ who-key name-def-hash-map ]
  (if (zero? (:fixed-cash (who-key name-def-hash-map)))
    (/ cash-to-divide cnt-persons-divisible-cash)
    (:fixed-cash (who-key name-def-hash-map))))

(defn list-of-items-bought-by [ who-key ]
  (:bought-items (who-key persons)))

(defn list-of-item-prices-bought-by [ who-key ]
  (map #(:price (% given-items)) (list-of-items-bought-by who-key)))

(defn total-price-of-items-bought-by [ who-key name-def-hash-map ]
  (reduce + (list-of-item-prices-bought-by who-key)))

(defn get-name [ who-key name-def-hash-map ]
  (:name (who-key name-def-hash-map)))

(defn values-of [ name-def-hash-map a-hash-map func-name func ]
  (map #( str func-name" "
              (get-name % name-def-hash-map)
              ": "
              (func % a-hash-map)
              "\n")
       (keys name-def-hash-map)))

(defn items-bought-by [ who-key name-def-has-map ]
  (map #(get-name % name-def-has-map)
       (list-of-items-bought-by who-key)))

(print (values-of persons persons     "cash-to-give-by" cash-to-give-by))
(print (values-of persons given-items "items-bought-by" items-bought-by))
(print (values-of persons persons     "total-price-of-items-bought-by" total-price-of-items-bought-by))

(def divisible-share-per-person 
  (/ (- total-price-given-items total-fixed-cash) cnt-persons-divisible-cash))

(println "divisible-share-per-person:" divisible-share-per-person)

(defn divisible-share-of [ who-key name-def-hash-map]
  (if (zero? (:fixed-cash (who-key name-def-hash-map) ))
    (- divisible-share-per-person
       (+ (reduce + (list-of-item-prices-bought-by who-key))
          (:given-cash (who-key name-def-hash-map))))
    0))

(print (values-of persons persons "divisible-share-of" divisible-share-of))

;; Separation of concerns:
;; Do not make the calculation of payment-amount and payment-direction
;; (who -> whom) dependent on each other

(defn payment-direction [ who-key name-def-hash-map ]
  (if (< (divisible-share-of who-key name-def-hash-map) 0)
    " gets "
    " pays "))

(defn payment-amount [ who-key name-def-hash-map ]
  (let [share (divisible-share-of who-key name-def-hash-map)]
    (if (< share 0)
      (* share -1)  ; just inver the negative number
      (if (zero? share)
        (:fixed-cash (who-key name-def-hash-map)) 
        share))))

(defn payment [ who-key name-def-hash-map]
  (str (get-name who-key name-def-hash-map)
       (payment-direction who-key name-def-hash-map)
       (payment-amount who-key name-def-hash-map)
       "\n"))

(println (map #(payment % persons) (keys persons)))
