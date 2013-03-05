(ns hochzeit.core)

(def persons (hash-map :bost         (hash-map :name "Bost"           :fixed-cash 0.0)
                       :michael      (hash-map :name "Michael"        :fixed-cash 0.0)
                       :andreas      (hash-map :name "Andreas"        :fixed-cash 0.0)
                       :rosa-carsten (hash-map :name "Rosa & Carsten" :fixed-cash 20.0)))

(def gifts (hash-map :vases 135.0
                     :flowers 15.0
                     :greeting-card 2.75
                     :fixed-cash 100.0))

(defn list-of [a-key] 
  "Returns a list of values under a key i.e. (0.0 20.0 0.0 0.0)"
  (map #(a-key %) (vals persons)))
(println "list-of fixed-cash: " (list-of :fixed-cash))


(def total-fixed-cash 
  (reduce + (list-of :fixed-cash)))
(str "total-fixed-cash: " total-fixed-cash)

(def cash-to-divide (- (:fixed-cash gifts) total-fixed-cash))
(str "cash-to-divide: " cash-to-divide)

(def cnt-floating-cash-persons (count (filter zero? fixed-cash-list)))
(str "cnt-floating-cash-persons: " cnt-floating-cash-persons)

(defn share-of [ who ]
  (let [cash (:fixed-cash (who persons))]
       (if (zero? cash)
         (/ cash-to-divide cnt-floating-cash-persons)
         cash)))

(str "share-of " (:name (:rosa-carsten persons)) ": " (share-of :rosa-carsten))
(str "share-of " (:name (:bost persons)) ": " (share-of :bost))

(map #(share-of %) (keys persons))
