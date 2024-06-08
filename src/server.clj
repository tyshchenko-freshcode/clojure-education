(ns server
  (:require [ring.adapter.jetty :as jetty]
            [hiccup2.core :as h]
            [hiccup.element :as e]
            [hiccup.page :as page]
            [compojure.core :as cmp]
            [compojure.route :as route]))

(defonce state
  (atom
   {:articles
    [{:id "1" :title "Dummy article 1" :content "Lorem"}
     {:id "2" :title "Dummy article 2" :content "Lorem"}
     {:id "3" :title "Dummy article 3" :content "Lorem"}
     {:id "4" :title "Dummy article 4" :content "Lorem"}]}))

(def articles (:articles @state))

(defn get-article-item [{:keys [id title]}] 
  [:li [:a {:href (str "/articles/" id)} title]])

(defn home-page []
  (str (page/html5 
        ;; TODO fix css not loading
        [:head (page/include-css "./styles/reset.css" "./styles/bootstrap.min.css")]
        [:h1 "Home page"]
        [:ol (map get-article-item articles)])))

(defn article-page [id]
  (let [data (filter (fn [a] (= (:id a) id)) articles)]
    (if (empty? data)
      (page/html5 [:h1 "Article not found"])
      (let [article (first data)]
        (page/html5 [:h1 (:title article)] [:p (:content article)])))))

(cmp/defroutes routes
  (cmp/GET "/" [] (home-page))
  (cmp/GET "/articles/:id" [id] (article-page id))
  (route/not-found "Page not found!"))

(defonce server (jetty/run-jetty #'routes {:port 3000
                                           :join? false}))