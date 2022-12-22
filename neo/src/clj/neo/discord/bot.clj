(ns neo.discord.bot
  (:require [clojure.core.async :as a]
            [discljord.connections :as c]
            [discljord.messaging :as m]
            [clojure.string :as s]))

(def intents #{:guilds :guild-messages :guild-members :guild-scheduled-events})

(defn event->command [event-data]
  (-> event-data
      :content
      (s/split #" ")
      first
      (subs 1)
      keyword))

(defn event->command-msg [event-data]
  (-> event-data
      :content
      (s/split #" " 2)
      second))

(defmulti handle-event (fn [event-type event-data] event-type))

(defmulti handle-command event->command)

(defn- bot-loop [{:keys [event-ch message-ch connection-ch]}]
  (loop []
    (let [[event-type event-data] (a/<!! event-ch)]
      (try
        (handle-event event-type event-data)
        (catch Exception e
          (println e)
          (c/disconnect-bot! connection-ch)))
      (when-not (= :disconnect event-type)
        (recur))))
  (m/stop-connection! message-ch)
  (a/close! event-ch))

(defn start! [config]
  (let [token (:bot-token config)
        event-ch (a/chan 100)
        bot {:event-ch      event-ch
             :connection-ch (c/connect-bot! token event-ch :intents intents)
             :message-ch    (m/start-connection! token)
             :config        config}]
    (assoc bot :running-task (future (bot-loop bot)))))

(defn stop! [{:keys [connection-ch running-task]}]
  (c/disconnect-bot! connection-ch)
  @running-task)

(defmethod handle-event :message-create [_ event-data]
  (when (and (s/starts-with? (:content event-data) "!") (not (:bot (:author event-data))))
    (handle-command event-data)))

(defmethod handle-event :default [_ _])

(defmethod handle-command :default [_])


