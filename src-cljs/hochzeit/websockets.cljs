(ns hochzeit.websockets
  (:require [hochzeit.bar :as bar]
            ;;[incanter.stats :as stats]
            ))

(defn log [& args]
  "TODO it might be better to turn fn log to a macro"
  (.log js/console
        (nth args 0) (nth args 1) (nth args 2) (nth args 3)))

(def server-url "ws://localhost:8080")
(def serverUrl server-url)
(def ws (new js/WebSocket server-url))

(set! (.-onclose ws)
      (fn [e]
        (log "WebWS closed")))

(set! (.-onerror ws)
      (fn [e]
        (log "WebWS error. Websocket server started?")))

(defn on-open [e]
  (.send ws "Jim")
  (.send ws "Text"))

(set! (.-onopen ws) on-open)


(defn copy [obj]
  "See http://stackoverflow.com/questions/122102/most-efficient-way-to-clone-an-object"
  (.parse js/JSON (.stringify js/JSON obj)))


(defn data-vals [data]
  (into [] (for [e data]
             (.-value e))))

(defn scale-factor [data]
  "Compute scale factor out of data"
  (let [v (data-vals data)]
    (.log js/console (apply min v))
    (.log js/console (apply max v))
    ;; (.log js/console (stats/median v))
  300))

(defn scale! [data factor]
  (doall  ; doall must be executed here
   (for [elem data]
     (set! (.-value elem) (* (.-value elem) factor)))))

(defn on-message [e]
  (let [obj (.parse js/JSON (.-data e))
        obj-copy (copy obj)]
    (.log js/console "obj: " obj)
    (scale! obj-copy (scale-factor obj))
    (bar/bar-chart js/d3 obj-copy)))
  
(set! (.-onmessage ws) on-message)


(defn connect []
  (.send ws "Jim"))

(set! (.-connect ws) connect)


(.log js/console "ns executed: hochzeit.websockets")
