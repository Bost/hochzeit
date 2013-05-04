(ns hochzeit.fix
  (:require [me.raynes.fs :as fs]
            [clj-time.core :as tco]
            [clj-time.coerce :as tce]
            [clj-time.format :as tf]
            [hochzeit.download :as d]))

(def c-save-dir     d/c-save-dir)
(def c-fsep d/c-fsep)

(defn subdirs [path]
  (into []
        (map #(str path % c-fsep) (into [] (sort (fs/list-dir path))))))

(defn filepath [path]
  (let [files (into [] (fs/list-dir path))]
    (into [] (map #(str path %) files))))

(def all-filepaths
  (let [paths
        ;(reduce into [] (map #(subdirs %) (subdirs (str c-save-dir "2013" c-fsep))))]
        [;"/home/bost/vircurex/2013/04/14/"
         "/home/bost/vircurex/2013/04/15/"
         ;"/home/bost/vircurex/2013/04/16/" "/home/bost/vircurex/2013/04/17/" "/home/bost/vircurex/2013/04/18/"
         ;"/home/bost/vircurex/2013/04/19/" "/home/bost/vircurex/2013/04/20/" "/home/bost/vircurex/2013/04/21/"
         ;"/home/bost/vircurex/2013/04/22/" "/home/bost/vircurex/2013/04/23/" "/home/bost/vircurex/2013/04/24/"
         ;"/home/bost/vircurex/2013/04/25/" "/home/bost/vircurex/2013/04/26/" "/home/bost/vircurex/2013/04/27/"
         ;"/home/bost/vircurex/2013/04/28/" "/home/bost/vircurex/2013/04/29/" "/home/bost/vircurex/2013/04/30/"
         ;"/home/bost/vircurex/2013/05/01/" "/home/bost/vircurex/2013/05/02/" "/home/bost/vircurex/2013/05/03/"
         ;"/home/bost/vircurex/2013/05/04/"
       ]]
    (reduce into [] (map #(filepath %) paths))))


(defn mod-hour [filepath]
  (let [time (fs/mod-time filepath)]
    (tco/hour (tce/from-long time))))

(defn mod-hours [filepaths]
  (let [times (into [] (map #(fs/mod-time %) filepaths))]
    (into [] (map #(tco/hour %)
                  (into [] (map #(tce/from-long %) times))))))


(def c-str-fmt-name d/c-str-fmt-name)
(def c-base-fname d/c-base-fname)


(def begin (+ (.length c-base-fname) 1 4 1 2 1 2 1))
(def end (+ begin 2))


(defn fname-hour [filepath]
  (Integer. (.substring (fs/base-name filepath) begin end)))


(defn s-fname-hours [filepaths]
  (into [] (map #(fname-hour %) filepaths)))


(defn fname-hours [filepaths]
  (into [] (map #(Integer. %) (s-fname-hours filepaths))))

;(fname-hours all-filepaths)
;(mod-hours all-filepaths)

(defn modify? [time0 time1]
  (= (+ time0 12) time1))


(defn pairs [filepaths]  (map vector (fname-hours filepaths) (mod-hours filepaths)))

(defn new-fname [filepath]
  (modify? (fname-hour filepath)
           (mod-hour filepath)))

(def f "/home/bost/vircurex/2013/04/15/vircurex.2013-04-15_06-50-09.xml")
(new-fname f)

;(map #(modify? (first %) (second %)) (pairs all-filepaths))




























