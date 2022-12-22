(ns neo.discord.api
  (:require [clj-http.client :as http]
            [neo.util.web :as web]))

(def discord-auth-endpoint "https://discord.com/oauth2/authorize")
(def discord-token-endpoint "https://discord.com/api/oauth2/token")
(def discord-revoke-endpoint "https://discord.com/api/oauth2/token/revoke")
(def scopes-string "identify")

(defn make-auth-link [redirect-url client-id random-state]
  (web/make-url discord-auth-endpoint {:client_id     client-id
                                       :redirect_uri  redirect-url
                                       :response_type "code"
                                       :scope         scopes-string
                                       :state         random-state}))

(defn fetch-tokens [redirect-url auth-code client-id client-secret]
  (http/post discord-token-endpoint
             {:as :json
              :accept :json
              :form-params {:client_id     client-id
                            :client_secret client-secret
                            :grant_type    "authorization_code"
                            :code          auth-code
                            :redirect_uri  redirect-url}}))