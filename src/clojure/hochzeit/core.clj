(ns hochzeit.core)

;{}  hash-map
;#{} set, hash-set
;()  list
;[]  vector

(def given-items {:vases         {:price 135.0 :name "Vasen" }
                  :flowers       {:price 15.0  :name "Blumen" }
                  :greeting-card {:price 2.75  :name "Hochszeitskarte" }
                  :cash          {:price 100.0 :name "Bargeld" }})

(def persons {:bost         {:name "Bost"
                             :given-cash 0.0
                             :fixed-cash 0.0
                             :bought-items '( :vases :flowers :greeting-card)}
              :michael      {:name "Michael"
                             :given-cash 50.0
                             :fixed-cash 0.0
                             :bought-items '()}
              :andreas      {:name "Andreas"
                             :given-cash 50.0
                             :fixed-cash 0.0
                             :bought-item'()}
              :rosa-carsten {:name "RosaCarsten"
                             :given-cash 0.0
                             :fixed-cash 20.0
                             :bought-items '()}})

(defn list-of [ a-key name-defs ]
  (map #(a-key %) (vals name-defs)))

(println "list-of names:"       (list-of :name persons))
(println "list-of given-cash"   (list-of :given-cash persons))
(println "list-of fixed-cash"   (list-of :fixed-cash persons))
; TODO how to concatenate lists?
(println "list-of bought-items" (concat (list-of :bought-items persons)))
(println "list-of given-items"  (list-of :name given-items))


(defn total [ cash-key name-defs ]
  (reduce + (list-of cash-key name-defs)))

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

(defn cash-to-give-by [ who-key name-defs ]
  (if (zero? (:fixed-cash (who-key name-defs)))
    (/ cash-to-divide cnt-persons-divisible-cash)
    (:fixed-cash (who-key name-defs))))

(defn list-of-items-bought-by [ who-key ]
  (:bought-items (who-key persons)))

(defn list-of-item-prices-bought-by [ who-key ]
  (map #(:price (% given-items)) (list-of-items-bought-by who-key)))

(defn total-price-of-items-bought-by [ who-key name-defs ]
  (reduce + (list-of-item-prices-bought-by who-key)))

(defn get-name [ who-key name-defs ]
  (:name (who-key name-defs)))

(defn items-bought-by [ who-key name-def-has-map ]
  (map #(get-name % name-def-has-map)
       (list-of-items-bought-by who-key)))

(defn values-of [ name-defs values func-name func ]
  "Returns values of a function func applied to the hash-map values using name definition from hash-map name-defs"
  (map #( str func-name" "
              (get-name % name-defs)
              ": "
              (func % values)
              "\n")
       (keys name-defs)))

;; use func as a func-name in the function values-of
(defmacro m-values-of [ name-defs values func ]
  `(let [func# ~func]
     (values-of ~name-defs ~values '~func func#)))

(print (m-values-of persons persons     cash-to-give-by))
(print (m-values-of persons given-items items-bought-by))
(print (m-values-of persons persons     total-price-of-items-bought-by))

(def divisible-share-per-person
  (/ (- total-price-given-items total-fixed-cash) cnt-persons-divisible-cash))

(println "divisible-share-per-person:" divisible-share-per-person)

(defn divisible-share-of [ who-key name-defs]
  (if (zero? (:fixed-cash (who-key name-defs) ))
    (- divisible-share-per-person
       (+ (reduce + (list-of-item-prices-bought-by who-key))
          (:given-cash (who-key name-defs))))
    0))

(print (m-values-of persons persons divisible-share-of))

;; Separation of concerns:
;; Do not make the calculation of payment-amount and payment-direction
;; (who -> whom) dependent on each other

(defn payment-direction [ who-key name-defs ]
  (if (< (divisible-share-of who-key name-defs) 0)
    " gets "
    " pays "))

(defn payment-amount [ who-key name-defs ]
  (let [share (divisible-share-of who-key name-defs)]
    (if (< share 0)
      (* share -1)  ; just inver the negative number
      (if (zero? share)
        (:fixed-cash (who-key name-defs))
        share))))

(defn payment [ who-key name-defs]
  (str (get-name who-key name-defs)
       (payment-direction who-key name-defs)
       (payment-amount who-key name-defs)
       "\n"))

(println (map #(payment % persons) (keys persons)))
