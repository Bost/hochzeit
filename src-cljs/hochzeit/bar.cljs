(ns hochzeit.bar)

;; (defn log [& args]
;;   "TODO it might be better to turn fn log to a macro"
;;   (.log js/console
;;         (nth args 0) (nth args 1) (nth args 2) (nth args 3)))

(defn getVals [vals i]
  (nth vals i))

(defn ffx [d3 w]
  (-> (.linear (.-scale d3))
      (.domain (array 0 1))
      (.range (array 0 w))))

(defn fx [d3 w i]
  (fn [d i]
    (- ((ffx d3 w) i)
       0.5)))

(defn ffy [d3 h]
  (-> (.linear (.-scale d3))
      (.domain (array 0 100))
      (.range (array 0 h))))

(defn fy [d3 h]
  (fn [dv] ((ffy d3 h) dv)))

(defn fheight [d3 h d]
  ((fy d3 h) d))

(defn data-range [d3 vals-len next]
  (-> (.range d3 vals-len)
      (.map next)))

(defn chart [d3 vals next w h i prm-fy]
 (let [data (data-range d3 (.-length vals) next)] ; starting dataset
   (-> (.select d3 "body")
       (.append "svg")
       (.attr "class" "chart")
       (.attr "width" (- (* w (.-length data))
                         1))
       (.attr "height" h)

       ;;;;;;;;;;;;;;;;;;
       (.selectAll "rect")
       (.data data)
       (.enter)
       (.append "rect")
       (.attr "x" (fx d3 w i))
       (.attr "y" prm-fy)
       (.attr "width" w)
       (.attr "height" (fheight d3 h
                                (.-value (nth vals i))))
       
       ;;;;;;;;;;;;;;;;;;
       (.append "line")
       (.attr "x1" 0)
       (.attr "x2" (* w (.-length data)))
       (.attr "y1" (- h 0.5))
       (.attr "y2" (- h 0.5))
       (.style "stroke" "#000")
       )))

;; (defn bar [vals]
;;   (def valsLen (count vals))
;;   (log valsLen)
;;   )

(js/console.log "ns executed: hochzeit.bar")
