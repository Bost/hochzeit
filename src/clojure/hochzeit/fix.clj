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
        (reduce into [] (map #(subdirs %) (subdirs (str c-save-dir "2013" c-fsep))))]
        ;[;"/home/bost/vircurex/2013/04/14/"
         ;"/home/bost/vircurex/2013/04/15/"
         ;"/home/bost/vircurex/2013/04/16/" "/home/bost/vircurex/2013/04/17/" "/home/bost/vircurex/2013/04/18/"
         ;"/home/bost/vircurex/2013/04/19/" "/home/bost/vircurex/2013/04/20/" "/home/bost/vircurex/2013/04/21/"
         ;"/home/bost/vircurex/2013/04/22/" "/home/bost/vircurex/2013/04/23/" "/home/bost/vircurex/2013/04/24/"
         ;"/home/bost/vircurex/2013/04/25/" "/home/bost/vircurex/2013/04/26/" "/home/bost/vircurex/2013/04/27/"
         ;"/home/bost/vircurex/2013/04/28/" "/home/bost/vircurex/2013/04/29/" "/home/bost/vircurex/2013/04/30/"
         ;"/home/bost/vircurex/2013/05/01/" "/home/bost/vircurex/2013/05/02/" "/home/bost/vircurex/2013/05/03/"
         ;"/home/bost/vircurex/2013/05/04/"
       ;]]
    (reduce into [] (map #(filepath %) paths))))

(defn mod-hour [filepath]
  (let [time (fs/mod-time filepath)]
    (tco/hour (tce/from-long time))))

(def c-str-fmt-dir d/c-str-fmt-dir)
(def c-str-fmt-name d/c-str-fmt-name)
(def c-base-fname d/c-base-fname)
(def dirname-length (+
                     (.length c-save-dir)
                     (.length c-str-fmt-dir)))

(def begin (+ (.length c-base-fname) 1 4 1 2 1 2 1))
(def end (+ begin 2))

(defn fname-hour [filepath]
  (Integer. (.substring (fs/base-name filepath) begin end)))

(defn modify? [time0 time1]
  (= (+ time0 12) time1))

(defn new-fname [filepath]
  (let [f-hour (fname-hour filepath)
        m-hour (mod-hour filepath)
        base-name (fs/base-name filepath)
        base-name-length (.length base-name)]
    (if (modify? f-hour m-hour)
      [filepath
       (str (.substring filepath 0 (- (.length filepath) base-name-length))
            (.substring base-name 0 begin)
            m-hour
            (.substring base-name end base-name-length))])))


(def mv-pairs (into [] (remove nil? (map #(new-fname %) all-filepaths))))

(defn do-println [src dst]
  (println "cp -p" src dst)
  (println "rm" src))

(defn -main[]
  (println "#!/bin/bash -e")
  (dorun (map #(do-println (first %) (second %)) mv-pairs)))
