(ns material-desktop.app
  (:require
   [reagent.core :as r]

   [material-desktop.init :as init]
   [material-desktop.ddapi-integration :as ddapi-integration]
   [material-desktop.components :as mdc]))




(defonce !ui-root-component (atom [:div "ui-root-component"]))



(defn mount-app []
  (r/render
   [mdc/ErrorBoundary
    @!ui-root-component]
   (js/document.getElementById "app")))


(defn reload []
  (ddapi-integration/integrate-ddapis-into-re-frame)
  (mount-app))


(defn start [ui-root-component]
  (reset! !ui-root-component ui-root-component)
  (init/install-roboto-css)
  (ddapi-integration/integrate-ddapis-into-re-frame)
  (mount-app))
