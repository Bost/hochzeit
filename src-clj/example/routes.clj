(ns example.routes
  (:use compojure.core
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [example.views :as views]
            [compojure.handler :as handler]
            [compojure.response :as response]))

(defroutes main-routes
  ;; (GET "/" [] (resp/file-response "index.html" {:root "public"}))
  ;; (GET "/" [] (ring-resp/resource-response "index.html" {:root "public"}))

  (GET "/" [] (views/index-page))
  (GET "/repl-demo" [] (views/repl-demo-page))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
