(ns hochzeit.core)

(def gifts (hash-map :vases 135.0
                     :flowers 15.0
                     :greeting-card 2.75
                     :cash 100.0))

(def persons
  (hash-map :bost         (hash-map :name "Bost"        
                                    :cash-given 0.0
                                    :gifts-bought '( :vases
                                                     :flowers
                                                     :greeting-card))
            :michael      (hash-map :name "Michael"     
                                    :cash-given 0.0
                                    :gifts-bought '())
            :andreas      (hash-map :name "Andreas"     
                                    :cash-given 0.0
                                    :gifts-bought '())
            :rosa-carsten (hash-map :name "RosaCarsten" 
                                    :cash-given 20.0
                                    :gifts-bought '())))

(defn list-of [a-key] 
  "Returns a list of values under a key i.e. (0.0 20.0 0.0 0.0)"
  (map #(a-key %) (vals persons)))
(println "list-of given cash" (list-of :cash-given))
(println "list-of names:" (list-of :name))
(println "list-of all gifts" (keys gifts))
; TODO how to concatenate lists?
(println "list-of bought gifts" (concat (list-of :gifts-bought)))

(def total-prize-of-gifts 
  (reduce + (vals gifts)))
(println "total-prize-of-gifts" total-prize-of-gifts)

(def total-fixed-cash 
  (reduce + (list-of :cash-given)))
(println "total-fixed-cash:" total-fixed-cash)


(def cash-to-divide (- (:cash gifts) total-fixed-cash))
(println "cash-to-divide:" cash-to-divide)

(def cnt-floating-cash-persons (count (filter zero? (list-of :cash-given))))

(println "cnt-persons-giving-cash" cnt-floating-cash-persons)

(defn cash-share-of [ who-key ]
  (let [cash (:cash-given (who-key persons))]
       (if (zero? cash)
         (/ cash-to-divide cnt-floating-cash-persons)
         cash)))

(defn str-cash-share-of [who-key]
  (str "cash-share-of "
     (:name (who-key persons))": "(cash-share-of who-key)"\n"))

(println (map #(str-cash-share-of %) (keys persons)))

(defn gifts-bought-by [who-key]
  (:gifts-bought (who-key persons)))

(defn str-gifts-bought-by [who-key]
  (str "gifts-bought-by "(:name (who-key persons))": "
       (gifts-bought-by who-key)"\n"))
;(map #(cash-share-of %) (keys persons))

(println (map #(str-gifts-bought-by %) (keys persons)))

(defn prices-of-gifts-bought-by [who-key]
  (map #(% gifts) (gifts-bought-by who-key)))

(defn total-price-of-gifts-bought-by [who-key]
  (reduce + (prices-of-gifts-bought-by who-key)))

(defn str-price-of-gifts-bought-by [who-key]
  (str "total-price-of-gifts-bought-by "(:name (who-key persons))": "
       (total-price-of-gifts-bought-by who-key)))

(println (str-price-of-gifts-bought-by :rosa-carsten))
(println (str-price-of-gifts-bought-by :michael))

