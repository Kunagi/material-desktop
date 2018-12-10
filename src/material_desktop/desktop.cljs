(ns material-desktop.desktop
  (:require
   [cljs-react-material-ui.reagent :as mui]
   [cljs-react-material-ui.core :refer [create-mui-theme color]]
   [cljs-react-material-ui.icons :as icon]
   [re-frame.core :as rf]

   [material-desktop.components :as mdc]))

(def base-theme
  (create-mui-theme {:palette {:primary {:main (color :light-blue 700)}}
                     :secondary {:main (color :teal :A100)}
                     :text-color (color :common :white)
                     :typography {:use-next-variants true}}))

(defn desktop-app-bar []
  [mui/app-bar
   {:position "static"}
   [mui/toolbar
    [mui/icon-button
     {:color :inherit
      :style {:margin-left "-20px"
              :margin-right "20px"}}
     [icon/menu]]
    [mui/typography
     {:variant :title
      :style {:flex-grow "1"}
              ;:font-size "95%"}
      :color :inherit}
     @(rf/subscribe [::title])]

    (into [:div
           {:style {:display :flex}}]
          @(rf/subscribe [::appbar-toolbar-components]))]])


(defn desktop-workarea []
    [:div
     {:style {:width "800px"
              :margin "1em auto"}}
     (into [:div#workarea-pre-components]
           (map (fn [c] [mdc/error-boundary c])
                @(rf/subscribe [::workarea-pre-components])))
     (into [:div#workarea-components]
           (map (fn [c] [mdc/error-boundary c])
                @(rf/subscribe [::workarea-components])))
     (into [:div#dialogs]
           @(rf/subscribe [::dialogs]))])


(defn desktop []
  [mui/mui-theme-provider
   {:theme base-theme}
   [mui/css-baseline]
   [desktop-app-bar]
   [desktop-workarea]
   (into [:div#workarea-post-components]
         (map (fn [c] [mdc/error-boundary c])
              @(rf/subscribe [::workarea-post-components])))])


(defn desktop-subscription []
  [:div
   [:hr]
   [mdc/text-body1
    "subscription [::desktop] ->"]
   [mdc/edn @(rf/subscribe [::desktop])]])


;; re-frame subscriptions


(rf/reg-sub
 ::desktop
 (fn [db _]
   (get db ::desktop)))


(rf/reg-sub
 ::title
 :<- [::desktop]
 (fn [desktop _]
   (get-in desktop [:appbar :title])))


(defn reg-sub-desktop-components [subscription-name path]
  (rf/reg-sub
   subscription-name
   :<- [::desktop]
   (fn [desktop _]
     (-> desktop
         (get-in path)
         (vals)
         (->> (reduce into []))))))

(reg-sub-desktop-components ::dialogs [:dialogs])
(reg-sub-desktop-components ::appbar-toolbar-components [:appbar :toolbar-components])
(reg-sub-desktop-components ::workarea-pre-components [:workarea :pre-components])
(reg-sub-desktop-components ::workarea-components [:workarea :components])
(reg-sub-desktop-components ::workarea-post-components [:workarea :post-components])
