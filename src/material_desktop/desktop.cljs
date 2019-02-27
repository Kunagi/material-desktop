(ns material-desktop.desktop
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/core/styles" :refer [createMuiTheme withStyles]]
   ["@material-ui/icons" :as icons]
   [goog.object :as gobj]
   [re-frame.core :as rf]

   [material-desktop.desktop-api]
   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]
   [material-desktop.editing :as editing]))


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


(defn FormDialog_ [dialog]
  (let [form-query (:form-query dialog)
        form (if form-query (<subscribe (:form-query dialog)))]
    [editing/FormDialog
     :open? (-> dialog :open?)
     :form form]))


(defn FormDialog []
  (let [dialog (<subscribe [:desktop/form-dialog])]
    [FormDialog_ dialog]))


(defn Desktop [& {:as options :keys [appbar
                                     workarea]}]
  [:div
   {:style {:font-family "\"Roboto\", \"Helvetica\", \"Arial\", sans-serif"
            :color "#333"}}
   [:> mui/CssBaseline]
   [:> mui/MuiThemeProvider
    {:theme base-theme}
    (into [DesktopAppBar] (apply concat appbar))
    (into [DesktopWorkarea] (apply concat workarea))
    [FormDialog]]])
    ;; (into [:div#workarea-post-components]
    ;;       (map (fn [c] [mdc/ErrorBoundary c])
    ;;            @(rf/subscribe [::workarea-post-components])))]])
