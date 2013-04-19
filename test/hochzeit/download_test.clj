(ns hochzeit.download-test
  (:use [clojure.test])
  (:require [hochzeit.download :as d]
            [clj-http.client :as client]
            ))

(prn "src-uri: " d/src-uri)
(prn "fmt-dir-s: " d/fmt-dir-s)
(prn "fmt-fname-s: " d/fmt-fname-s)
(prn "base-fname: " d/base-fname)
(prn "ext: " d/ext)
 
(def http-resp (client/get d/src-uri
                               {:decode-body-headers true :as :auto}))
(prn "http-resp: " http-resp)
(prn "resp-headers: " (resp-headers http-resp))
(prn "resp-date: " (resp-date http-resp))
(prn "dir: " (tf/unparse fmt-dir-s (resp-date http-resp)))
(prn "fname-date: " (tf/unparse fmt-fname-s (resp-date http-resp)))

;(prn "dst-uri!: " (dst-uri! save-dir
                            ;fmt-dir-s
                            ;fmt-fname-s
                            ;base-fname
                            ;(resp-date http-resp)
                            ;ext))
;(prn "download!: " (download! src-uri save-dir fmt-dir-s fmt-fname-s base-fname ext))

(def save-dir "/tmp")
(prn "save-dir: " save-dir)
(prn "-main: " (-main save-dir))
