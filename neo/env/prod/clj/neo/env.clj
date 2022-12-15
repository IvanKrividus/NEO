(ns neo.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[neo started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[neo has shut down successfully]=-"))
   :middleware identity})
