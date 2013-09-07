(ns example.routes
  (:use compojure.core
        example.views
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [ring.util.response :as ring-resp]))


(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                #(if (= "/" %) "/index.html" %)))))

(defroutes main-routes
  (GET "/" [] (index-page))

  ;; (GET "/" [] (resp/file-response "index.html" {:root "public"}))
  (GET "/a" [] (ring-resp/resource-response "index.html" {:root "public"}))

  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))
