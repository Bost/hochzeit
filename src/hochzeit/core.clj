(ns hochzeit.core)

(def given-items (hash-map :vases 135.0
                     :flowers 15.0
                     :greeting-card 2.75
                     :cash 100.0))

(def persons
  (hash-map :bost         (hash-map :name "Bost"        
                                    :given-cash 0.0
                                    :fixed-cash 0.0
                                    :items-bought '( :vases
                                                     :flowers
                                                     :greeting-card))
            :michael      (hash-map :name "Michael"     
                                    :given-cash 50.0
                                    :fixed-cash 0.0
                                    :items-bought '())
            :andreas      (hash-map :name "Andreas"     
                                    :given-cash 50.0
                                    :fixed-cash 0.0
                                    :items-bought '())
            :rosa-carsten (hash-map :name "RosaCarsten" 
                                    :given-cash 0.0
                                    :fixed-cash 20.0
                                    :items-bought '())))

(defn list-of [a-key] 
  (map #(a-key %) (vals persons)))

(println "list-of names:" (list-of :name))
(println "list-of given cash" (list-of :given-cash))
(println "list-of fixed cash" (list-of :fixed-cash))
; TODO how to concatenate lists?
(println "list-of bought items" (concat (list-of :items-bought)))
(println "list-of all given-items" (keys given-items))

(def price-of-given-items 
  (reduce + (vals given-items)))
(println "price-of-given-items" price-of-given-items)

(defn total [ cash-key] 
  (reduce + (list-of cash-key)))

(def total-given-cash (total :given-cash))
(println "total given-cash:" total-given-cash)

(def total-fixed-cash (total :fixed-cash))
(println "total fixed-cash:" total-fixed-cash)

(def cash-to-divide (- total-given-cash total-fixed-cash))
(println "cash-to-divide:" cash-to-divide)

(def cnt-persons-divisible-cash
  (count (filter zero? (list-of :fixed-cash))))

(println "cnt-persons-divisible-cash:" cnt-persons-divisible-cash)

(defn cash-to-give-by [ who-key ]
  (if (zero? (:fixed-cash (who-key persons)))
    (/ cash-to-divide cnt-persons-divisible-cash)
    (:fixed-cash (who-key persons))))

(defn items-bought-by [who-key]
  (:items-bought (who-key persons)))

(defn price-of-items-bought-by [who-key]
  (map #(% given-items) (items-bought-by who-key)))

(defn total-price-of-items-bought-by [who-key]
  (reduce + (price-of-items-bought-by who-key)))

(defn values-of [ info-str func]
  (map #( str info-str" "
              (:name (% persons))": "
              (func %)"\n")
       (keys persons)))

(print (values-of "cash-to-give-by" cash-to-give-by))
(print (values-of "items-bought-by" items-bought-by))
(print (values-of "total-price-of-items-bought-by" total-price-of-items-bought-by))

(def divisible-share-per-person 
  (/ (- price-of-given-items total-fixed-cash) cnt-persons-divisible-cash))

(println "divisible-share-per-person:" divisible-share-per-person)

(defn divisible-share-of [ who-key ]
  (if (zero? (:fixed-cash (who-key persons) ))
    (- divisible-share-per-person
       (+ (reduce + (price-of-items-bought-by who-key))
          (:given-cash (who-key persons))))
    0))

(print (values-of "divisible-share-of" divisible-share-of))

;; Separation of concerns:
;; Do not make the calculation of payment-amount and payment-direction
;; (who -> whom) dependent on each other

(defn payment-direction [ who-key ]
  (if (< (divisible-share-of who-key) 0)
    " gets "
    " pays "))

(defn payment-amount [ who-key ]
  (let [share (divisible-share-of who-key)]
    (if (< share 0)
      (* share -1)  ; just inver the negative number
      (if (zero? share)
        (:fixed-cash (who-key persons)) 
        share))))

(defn payment [ who-key ]
  (str (:name (who-key persons))
       (payment-direction who-key)
       (payment-amount who-key)
       "\n"))

(println (map #(payment %) (keys persons)))
