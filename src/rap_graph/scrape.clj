(ns rap-graph.scrape
  (:require [net.cgrand.enlive-html :as html]))

(defn get-page [url]
  (println "Scraping" url "...")
  (html/html-resource (java.net.URL. url)))

(defn url-name [url]
  (re-find #"[^\/]+$" url))

(defn extract-artist [artist-html]
  (let [name (html/text artist-html)
        url (:href (:attrs artist-html))
        url-name (url-name url)]
   url-name))

(defn find-artists [url]
  (let [page (get-page url)]
    (set (map extract-artist
              (html/select page #{[:div.featured_artists :a]
                                  [:h1.song_title :a]})))))

(defn artist-url [artist]
  (str "http://rapgenius.com/artists/" artist))

(def extract-song [song-html]
  (let [url (:href (:attrs song-html))]
    url))

(defn find-songs [artist-page]
  (map extract-song (html/select artist-page [:ul.song_list :a])))

(defn top-songs [artist]
  (let [url (artist-url artist)
        page (get-page url)
        songs (find-songs)]
    songs))
