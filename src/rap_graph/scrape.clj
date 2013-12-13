(ns rap-graph.scrape
  (:require [net.cgrand.enlive-html :as html]))

(defn get-page [url]
  (println "Scraping " url "...")
  (html/html-resource (java.net.URL. url)))

(defn url-name [url]
  (re-find #"[^\/]+$" url))

(defn extract-artist [artist-html]
  (let [name (html/text artist-html)
        url (:href (:attrs artist-html))
        url-name (url-name url)]
   url-name))

(defn find-artists [url]
  (let [page (get-page url)
        original-artist (url-name url)]
    (set (map extract-artist
              (html/select page #{[:div.featured_artists :a]
                                  [:h1.song_title :a]})))))
