(ns neo.routes.auth
  (:require [neo.layout :as layout]
            [neo.discord.middleware :as discord]
            [neo.middleware :as middleware]
            [neo.discord.api :as api]))

(defn account-link-page [request]
  (layout/render request "accountlink.html"))

(defn discord-authorized-page [request {:keys [client-id client-secret redirect-url]}]
  (let [state (get-in request [:session :discord-oauth-random-state])
        received-state (get-in request [:query-params "state"])]
    (clojure.pprint/pprint request)
    (if (or (nil? state) (not= state received-state))
      (layout/render request "error.html" {:status 403 :title "error" :message "Bad state received from Discord authorization server"})
      (let [auth-code (get-in request [:query-params "code"])]
        (clojure.pprint/pprint (api/fetch-tokens redirect-url auth-code client-id client-secret))
        (layout/render request "accountlink.html")))))

(defn auth-routes [config]
  (let [discord-config (:discord-api config)
        esi-config (:esi-api config)]
    [""
     {:middleware [middleware/wrap-csrf
                   middleware/wrap-formats]}
     ["/accountLink" {:get        account-link-page
                      :middleware [[discord/wrap-discord-auth discord-config]]}]
     ["/discordAuthorized" {:get #(discord-authorized-page % discord-config)}]]))