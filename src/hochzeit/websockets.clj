(ns hochzeit.websockets
  (:use [lamina.core]
	[aleph.http])
  (:require [cheshire.core :refer :all])
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
    [{:time 1297110662 :value 33},
     {:time 1297110663 :value 88},
     {:time 1297110663 :value 51},
     {:time 1297110664 :value 53},
     {:time 1297110665 :value 58},
     {:time 1297110666 :value 59},]
    )
   )

(defn chat-handler [ch handshake]
  (receive ch
	   (fn [name]
	     (println (str name " just logged in"))
	     (siphon (map* #(render-msg name %) ch) broadcast-channel)
	     (siphon broadcast-channel ch))))

(defn s []
     (start-http-server chat-handler {:port 8080 :websocket true}))
