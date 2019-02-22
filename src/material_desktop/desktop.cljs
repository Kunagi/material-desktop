(ns material-desktop.desktop
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/core/styles" :refer [createMuiTheme withStyles]]
   ["@material-ui/icons" :as icons]
   [goog.object :as gobj]
   [re-frame.core :as rf]

   [material-desktop.components :as mdc]))

;; (def base-theme
;;   (createMuiTheme {:palette {:primary {:main (color :light-blue 700)}}
;;                    :secondary {:main (color :teal :A100)}
;;                    :text-color (color :common :white)
;;                    :typography {:use-next-variants true}}))


(def theme {:palette mdc/palette
            :typography {:useNextVariants true}})

(def base-theme (createMuiTheme (clj->js theme)))


(defn DesktopAppBar [& {:as options :keys [title
                                           toolbar-components]}]
  [:> mui/AppBar
   {:position "static"}
   [:> mui/Toolbar
    [:> mui/IconButton
     {:color :inherit
      :style {:margin-left "-20px"
              :margin-right "20px"}}
     [:> icons/Menu]]
    [:> mui/Typography
     {:variant :h5
      :style {:flex-grow 1}
              ;:font-size "95%"}
      :color :inherit}
     title]

    (into [:div
           {:style {:display :flex}}]
          toolbar-components)]])


(defn DesktopWorkarea [& {:as options :keys [components]}]
  [:div
   {:style {:margin "1rem"}}
     ;; {:style {:width "800px"
     ;;          :margin "1em auto"}}
   ;; (into [:div#workarea-pre-components]
   ;;       (map (fn [c] [mdc/ErrorBoundary c])
   ;;            @(rf/subscribe [::workarea-pre-components])))
   (into [:div#workarea-components]
         (map (fn [c] [mdc/ErrorBoundary c])
              components))])
   ;; (into [:div#dialogs]
   ;;       @(rf/subscribe [::dialogs]))])


(defn Desktop [& {:as options :keys [appbar
                                     workarea]}]
  [:div
   {:style {:font-family "\"Roboto\", \"Helvetica\", \"Arial\", sans-serif"
            :color "#333"}}
   [:> mui/CssBaseline]
   [:> mui/MuiThemeProvider
    {:theme base-theme}
    ;[mui/css-baseline]
    (into [DesktopAppBar] (apply concat appbar))
    (into [DesktopWorkarea] (apply concat workarea))]])
    ;; (into [:div#workarea-post-components]
    ;;       (map (fn [c] [mdc/ErrorBoundary c])
    ;;            @(rf/subscribe [::workarea-post-components])))]])


(defn desktop-subscription []
  [:div
   [:hr]
   [mdc/Text
    "subscription [::desktop] ->"]
   [mdc/Data @(rf/subscribe [::desktop])]])


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
