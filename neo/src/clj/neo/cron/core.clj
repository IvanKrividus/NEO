(ns neo.cron.core
  (:require [tea-time.core :as tt]
            [mount.core :as mount]))

(mount/defstate
  ^{:on-lazy-start :throw
    :on-reload     :noop}
  schedule
  :start (do (println "Starting scheduler...") (tt/start!) (atom {}))
  :stop (do (println "Stopping scheduler...") (tt/stop!)))

(defn- ms->s [ms]
  (-> ms (* 1000) tt/micros->seconds))

(defn- clear-task [name]
  (swap! schedule dissoc name))

(defn schedule-repeating! [name start-delay-ms repeat-delay-ms task-fn]
  (when (contains? @schedule name)
    (throw (ex-info (str "Task with the name " name " already exists (accidentally scheduled twice?)")
                    {:name            name
                     :start-delay-ms  start-delay-ms
                     :repeat-delay-ms repeat-delay-ms})))
  (swap! schedule assoc name (if (nil? repeat-delay-ms)
                               (tt/after! (ms->s start-delay-ms) (bound-fn [] (do (clear-task name) (task-fn)))) ; clear the task first in case task-fn throws exception. TODO a  try-catch-finally is probably the best practice way to actually do this
                               (tt/every! (ms->s repeat-delay-ms) (ms->s start-delay-ms) (bound-fn [] (task-fn))))))

(defn schedule! [name start-delay-ms task-fn]
  (schedule-repeating! name start-delay-ms nil task-fn))

(defn cancel! [name]
  (when-let [task (@schedule name)]
    (tt/cancel! task)
    (clear-task name)))