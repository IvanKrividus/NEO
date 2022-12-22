(ns neo.discord.core
  (:require [neo.config :refer [env]]
            [neo.discord.bot :as bot]
            [mount.core :as mount]
            [discljord.messaging :as m]))

(mount/defstate bot
  :start (bot/start! (:discord-api env))
  :stop (bot/stop! bot))

(defn create-role! [{:keys [message-ch config]} role]
  @(m/create-guild-role! message-ch (:guild-id config) :name role))

(defn grant-role! [{:keys [message-ch config]} discord-user-id role-id]
  @(m/add-guild-member-role! message-ch (:guild-id config) discord-user-id role-id))

(defn revoke-role! [{:keys [message-ch config]} discord-user-id role-id]
  @(m/remove-guild-member-role! message-ch (:guild-id config) discord-user-id role-id))

(defn enumerate-roles! [{:keys [message-ch config]}]
  @(m/get-guild-roles! message-ch (:guild-id config)))