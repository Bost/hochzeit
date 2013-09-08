(ns example.hello
  (:require
   ;; [hochzeit.crossover :as co]
   ;; (:require [hochzeit.core :as co]
   ;; [clojure.java.io :as io]
  )
  (:require-macros [hochzeit.macros :as ma])
  ;; (:import java.io.File)
)

(def txt
  (ma/load-template "resources/public/test.txt"))

;; (defn r []
;;   (with-open [rdr (io/reader "/tmp/test.txt" )]
;;     (doseq [line (line-seq rdr)]
;;       (str line))))

;; ;; (js/console.log (co/s))
;; (js/console.log (r))
(js/console.log txt)

;; (js/console.log (-> (java.io.File. ".") .getAbsolutePath)
;;                 ;; (System/getProperty "user.dir")
;;                 )
(def valsx
  (cljs.core/clj->js

   ;; (co/e)

   ;; (into [] (co/e [[:EUR :BTC] [:PPC :USD]]
   ;;               ["/home/bost/vircurex-flat/vircurex.2013-05-17_03-05-04.xml"
   ;;                "/home/bost/vircurex-flat/vircurex.2013-04-14_11-50-26.xml"]))


   [{:time 1297110662 :value 88},
    {:time 1297110663 :value 24},
    {:time 1297110663 :value 51},
    {:time 1297110664 :value 53},
    {:time 1297110665 :value 58},
    {:time 1297110666 :value 59},]

   ))

(js/console.log "ns example.hello executed")
