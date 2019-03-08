(ns material-desktop.app
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [re-frame.core :as rf]

   [material-desktop.init :as init]
   [material-desktop.ddapi-integration :as ddapi-integration]
   [material-desktop.components :as mdc]
   [material-desktop.desktop.navigation :refer [integrate!]]))

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
  (integrate!)
  (ddapi-integration/integrate-ddapis-into-re-frame)
  (mount-app))


