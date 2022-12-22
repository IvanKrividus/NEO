(ns neo.discord.middleware
  (:require [ring.util.response :as response]
            [neo.discord.api :as api])
  (:import (java.security SecureRandom)))

(defmacro pt [expr]
  `(let [result# ~expr]
     (clojure.pprint/pprint result#)
     result#))

(defn wrap-discord-auth [handler {:keys [redirect-url client-id]}]
  (fn [request]
    (let [session (:session request)
          discord-creds (:discord-credentials request)]
      (if (some? discord-creds)
        (handler (assoc request :discord-token (:access-token discord-creds)))
        (let [random-state (as-> (SecureRandom.) $ (BigInteger. 256 $) (.toString $ 32))
              auth-url (api/make-auth-link redirect-url client-id random-state)]
          {:status 302
           :body ""
           :headers {"Location" auth-url}
           :session (assoc session :discord-oauth-random-state random-state)})))))