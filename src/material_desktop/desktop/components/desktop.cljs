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
   [material-desktop.desktop.components.form-dialog :as form-dialog]
   [material-desktop.desktop.components.menu-dialog :as menu-dialog]))


(def theme {:palette mdc/palette
            :typography {:useNextVariants true}})

(def base-theme (createMuiTheme (clj->js theme)))


(defn DesktopAppBar [{:as options :keys [title
                                         toolbar-components]}
                     page-args]
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
          ;;TODO page-args
          toolbar-components)]])


(defn DesktopWorkarea
  [{:as options :keys [components]}
   page-args]
  [:div
   {:style {:margin "1rem"}}
     ;; {:style {:width "800px"
     ;;          :margin "1em auto"}}
   ;; (into [:div#workarea-pre-components]
   ;;       (map (fn [c] [mdc/ErrorBoundary c])
   ;;            @(rf/subscribe [::workarea-pre-components])))
   (into [:div#workarea-components]
         (map (fn [c] [mdc/ErrorBoundary (conj c page-args)])
              components))])
   ;; (into [:div#dialogs]
   ;;       @(rf/subscribe [::dialogs]))])



(defn Desktop [{:as options :keys [appbar
                                   workarea
                                   page-args]}]
  [:div
   {:style {:font-family "\"Roboto\", \"Helvetica\", \"Arial\", sans-serif"
            :color "#333"}}
   [:> mui/CssBaseline]
   [:> mui/MuiThemeProvider
    {:theme base-theme}
    [DesktopAppBar appbar page-args]
    [DesktopWorkarea workarea page-args]
    ;;[menu-dialog/MenuDialog!]
    [form-dialog/FormDialog!]]])


(defn PagedDesktop [{:as options :keys [appbar
                                        pages
                                        home-page]}]
  (let [current-page-info (<subscribe [:material-desktop/current-page])
        current-page-key (:key current-page-info)
        current-page (get pages current-page-key)
        args (:args current-page-info)
        toolbar-components (-> []
                               (into (-> current-page :appbar :toolbar-components))
                               (into (-> appbar :toolbar-components)))
        current-page (assoc-in current-page [:appbar :toolbar-components] toolbar-components)]
    [Desktop (-> current-page
                 (assoc :page-args args))]))
