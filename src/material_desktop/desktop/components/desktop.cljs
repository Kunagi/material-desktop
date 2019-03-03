(ns material-desktop.desktop.components.desktop
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/core/styles" :refer [createMuiTheme withStyles]]
   ["@material-ui/icons" :as icons]
   [goog.object :as gobj]
   [re-frame.core :as rf]

   [material-desktop.desktop.subs]
   [material-desktop.desktop.events]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]
   [material-desktop.desktop.components.form-dialog :as form-dialog]))


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



(defn Desktop [{:as options :keys [appbar
                                   workarea]}]
  [:div
   {:style {:font-family "\"Roboto\", \"Helvetica\", \"Arial\", sans-serif"
            :color "#333"}}
   [:> mui/CssBaseline]
   [:> mui/MuiThemeProvider
    {:theme base-theme}
    (into [DesktopAppBar] (apply concat appbar))
    (into [DesktopWorkarea] (apply concat workarea))
    [form-dialog/FormDialog!]]])


(defn PagedDesktop [{:as options :keys [pages
                                        home-page]}]
  (let [current-page-key (<subscribe [:material-desktop/current-page-key])
        current-page-key (or current-page-key home-page)
        current-page (get pages current-page-key)]
    [Desktop current-page]))
