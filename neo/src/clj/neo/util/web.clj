(ns neo.util.web
  (:require [ring.util.codec :refer [url-encode]]))

(defn make-query-string [m]
  (->> (for [[k v] m]
         (str (url-encode (name k)) "=" (url-encode v)))
       (interpose "&")
       (apply str)))



(defn make-url
  ([base-url params-map]
   (str base-url
        (when params-map "?")
        (when params-map (make-query-string params-map))))
  ([base-url]
   (make-url base-url nil)))