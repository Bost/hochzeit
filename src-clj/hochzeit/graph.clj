(ns hochzeit.graph
  (:use ;[vijual :as v]           ; simple (console based) graph visualization
        [rhizome.viz]))

; TODO take a look at http://www.ryandesign.com/canviz/

(def g
    {:a [:b :c]
     :b [:c]
     :c [:a]})

 (view-graph (keys g) g
    :node->descriptor (fn [n] {:label n}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment
(def v (reduce into [] (cpv c-date c-save-dir)))
(def bv (map #(bigdec %) v))
(type bv)
(ist/mean bv)
)

;(ico/mean v)
;(ico/view (ich/histogram (ist/sample-normal 1000)))


;(v/draw-tree-image [[:north-america [:usa [:miami] [:seattle] [:idaho [:boise]]]] [:europe [:germany] [:france [:paris] [:lyon] [:cannes]]]])
;(v/draw-tree [[:north-america [:usa [:miami] [:seattle] [:idaho [:boise]]]] [:europe [:germany] [:france [:paris] [:lyon] [:cannes]]]])


;(v/draw-graph-image [[:a :b] [:b :c] [:c :d] [:a :d] [:e :f] [:a :f] [:g :e] [:d :e]])
;(v/draw-graph [[:a :b] [:b :c] [:c :d] [:a :d] [:e :f] [:a :f] [:g :e] [:d :e]])
;(pp/print-table [{:a 1 :b 2 :c 3} {:b 5 :a 7 :c "dog"}])