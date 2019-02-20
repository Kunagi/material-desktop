(ns ^:figwheel-hooks material-desktop.figwheel-adapter
  (:require
   [material-desktop.app :as app]
   [material-desktop.showcase]))


(defn ^:after-load on-figwheel-after-load []
  (tap> ::on-figwheel-after-load)
  (app/reload))
