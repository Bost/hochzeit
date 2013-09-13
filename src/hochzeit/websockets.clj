(ns hochzeit.websockets
  (:use [lamina.core]
	[aleph.http])
  (:require [cheshire.core :refer :all]
	    [hochzeit.core :as co]
	    )
  )

;; (def broadcast-channel (channel))

;; (defn chat-handler [ch handshake]
;;   (receive ch
;;     (fn [name]
;;       (siphon (map* #(str name ": " %) ch) broadcast-channel)
;;       (siphon broadcast-channel ch))))

;; (start-http-server chat-handler {:port 8008 :websocket true})

(def broadcast-channel (permanent-channel))

(defn render-msg [name msg]
  (println (str "name: " name "; message received from web: " msg))
   (generate-string
    ;; [{:time 1297110662 :value 33},
    ;;  {:time 1297110663 :value 88},
    ;;  {:time 1297110663 :value 51},
    ;;  {:time 1297110664 :value 53},
    ;;  {:time 1297110665 :value 58},
    ;;  {:time 1297110666 :value 59},]

    [{:time 20130517030504 :value 0.18},
     {:time 20130414115026 :value 0.12100001}]

    ;; (co/e [[:EUR :BTC] [:PPC :USD]]
    ;;       ["/home/bost/vircurex-flat/vircurex.2013-05-17_03-05-04.xml"
    ;;        "/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml"])
    )
   )

(defn chat-handler [ch handshake]
  (receive ch
	   (fn [name]
	     (println (str name " just logged in"))
	     (siphon (map* #(render-msg name %) ch) broadcast-channel)
	     (siphon broadcast-channel ch))))

(defn launch-server []
  "Convenience function. Start REPL, load this namespace, switch in it and launch:"
  "(launch-server)"
  (start-http-server chat-handler {:port 8080 :websocket true}))
