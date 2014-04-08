(ns rap-graph.scrape
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))

(defn get-page [url]
  ;(println "Scraping" url "...")
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

(defn song-url [song]
  (str "http://rapgenius.com" song))

(defn extract-song [song-html]
  (let [url (:href (:attrs song-html))]
    (song-url url)))

(defn is-collab? [artist song]
  (let [text (html/text song)
        url (:href (:attrs song))]
    (or (.contains text "Ft")
        (not (.startsWith url (str "/" artist))))))

(def illegal-words
  ["jwmt" "interview" "review"])

(defn is-valid? [song]
  (let [text (string/lower-case (html/text song))]
    (not (some (fn [word] (.contains text word)) illegal-words))))

(defn artist-songs-url [artist id page]
  (str "http://rapgenius.com/songs?for_artist_page=" id "&id=" artist "&lyrics_seo=false&page=" page "&pagination=true&search%5Bby_artist%5D=" id "&search%5Bunexplained_songs_last%5D%5B%5D=title&search%5Bunexplained_songs_last%5D%5B%5D=id"))

(defn find-artist-id [artist]
  (let [url (artist-url artist)
        page (get-page url)
        form-element (html/select page [:form.edit_artist])
        form-action (:action (:attrs (first form-element)))
        id (url-name form-action)]
    id))

(defn find-songs [artist]
  (let [artist-id (find-artist-id artist)]
    (loop [songs []
           page-number 1]
      (let [songs-url (artist-songs-url artist artist-id page-number)
            songs-page (get-page songs-url)
            songs-on-page (html/select songs-page [:ul.song_list :a])
            collab-songs (filter (partial is-collab? artist) songs-on-page)
            valid-songs (filter is-valid? collab-songs)
            extracted-songs (map extract-song valid-songs)]
        ;(println songs-on-page)
        (if (empty? songs-on-page)
          songs
          (recur (concat songs extracted-songs)
                 (inc page-number)))))))

(defn top-songs [artist]
  (let [url (artist-url artist)
        page (get-page url)
        songs (find-songs page)]
    songs))
