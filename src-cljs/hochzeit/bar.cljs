(ns hochzeit.bar)

(defn fn-width-range [d3 w]
  (-> (.linear (.-scale d3))
      (.domain (array 0 1))
      (.range (array 0 w))))

(defn fn-height-range [d3 h]
  (-> (.linear (.-scale d3))
      (.domain (array 0 100))
      (.range (array 0 h))))

(defn fn-x-range [d3 w i]
  (fn [d i]
    (- ((fn-width-range d3 w) i)
       0
       0.5)))

(defn fn-y-range [d3 h i]
  (fn [d i]
    (- h
       ((fn-height-range d3 h) (.-value d))
       0.5)))

(defn width [d3 w d]
  w)

(defn height [d3 h d]
  ((fn-height-range d3 h) d))

(defn fn-data-range [d3 vals-len next]
  (-> (.range d3 vals-len)
      (.map next)))

(defn plot-bar [d3 chart-elem chart-vals bar-i w h]
  (let [i (.indexOf chart-vals bar-i)]
    (-> chart-elem
	(.selectAll "rect")
	(.data chart-vals)
	(.enter)
	(.append "rect")
	(.attr "x"      (fn-x-range d3 w i))
	(.attr "y"      (fn-y-range d3 h i))
	(.attr "width"  (width      d3 w 0))
	(.attr "height" (height     d3 h (.-value bar-i)))

	;;;;;;;;;;;;;;;;;
	;; (.append "line")
	;; (.attr "x1" (* h 0))
	;; (.attr "x2" (* w (.-length chart-vals)))
	;; (.attr "y1" (- h 0.5))
	;; (.attr "y2" (- h 0.5))
	;; (.style "stroke" "#000")
	)))

(defn bar-chart [d3 chart-vals]
  (let [w 20 ; width
	h 80 ; height
        chart-elem (-> (.select d3 "body")
                       (.append "svg")
                       (.attr "class" "chart")
                       (.attr "width" (- (* w (.-length chart-vals))
                                         1))
                       (.attr "height" h))]
    (doseq [bar-i chart-vals] ; i-th value is i-th bar
      (plot-bar d3 chart-elem chart-vals bar-i w h))))


(js/console.log "ns executed: hochzeit.bar")
