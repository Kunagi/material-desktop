(ns ^:figwheel-hooks material-desktop.showcase
  (:require
   [cljsjs.material-ui]
   [reagent.core :as r]
   [re-frame.core :as rf]

   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop :as desktop]))


(defn show-frame [options component]
  (let [title (or (:title options) (str (first component)))]
    [:div
     {:style {:margin-top "10px"
              :color "#999"}}
     title
     [:div
      {:style {:background "#FFFFFF"
               :color "#333"}}
      component]]))

(defn showcase []
  [:div
   [show-frame
    {:title "data (map)"}
    [mdc/edn {:hello "world"}]]
   [show-frame
    {:title "data (vector)"}
    [mdc/edn ['hello ::world]]]])


(defn root-ui []
  [mdc/error-boundary
   [desktop/desktop]])


(defn mount-app []
  (r/render
   [root-ui]
   (js/document.getElementById "app")))


(defn ^:export start
  []
  (init/install-roboto-css)
  (rf/dispatch-sync [::init])
  (mount-app))


(defn ^:after-load on-figwheel-after-load []
  (rf/dispatch-sync [::init])
  (mount-app))


(rf/reg-event-db
 ::init
 (fn [db _]
   (-> db
       (assoc-in [::desktop/desktop :appbar :title] "Material Desktop Showcase")
       (assoc-in [::desktop/desktop :workarea :components :showcase] [[showcase]]))))


(start)
