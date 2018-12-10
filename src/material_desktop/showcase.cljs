(ns ^:figwheel-hooks material-desktop.showcase
  (:require
   [cljsjs.material-ui]
   [reagent.core :as r]
   [re-frame.core :as rf]

   [material-desktop.init :as init]))



(defn root-ui []
  [:div "hello world"])
   ;; [sysfail/sysfail-ui]
   ;; [v/error-boundary
   ;;  [desktop/desktop]]])


(defn mount-app []
  (r/render
   [root-ui]
   (js/document.getElementById "app")))


(defn ^:export start
  []
  (init/install-roboto-css)
  (mount-app))


(defn ^:after-load on-figwheel-after-load []
  (mount-app))


(start)
