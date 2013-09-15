(ns example.routes
  (:use [compojure.core]
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [example.views :as views]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [ring.util.response :as ring-resp]))


(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                #(if (= "/" %) "/index.html" %)))))

(defroutes main-routes
;;(GET "/" [] (resp/file-response "index.html" {:root "public"}))
  (GET "/" [] (ring-resp/resource-response "index.html" {:root "public"}))

  ;; TODO where is index-page defined?
  (GET "/repl-demo" [] (views/repl-demo-page))
  (GET "/index-page" [] (views/index-page))

  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
