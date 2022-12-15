(ns neo.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [neo.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[neo started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[neo has shut down successfully]=-"))
   :middleware wrap-dev})
