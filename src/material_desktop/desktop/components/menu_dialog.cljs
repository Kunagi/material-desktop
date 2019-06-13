(ns material-desktop.desktop.components.menu-dialog
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   ["@material-ui/core/colors" :as mui-colors]
   [re-frame.core :as rf]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]))


(defn MenuDialog [{:as options
                   :keys [open?
                          title
                          submit-text
                          cancel-event]
                   :or {submit-text "Submit"}}]
  [:div
   ;; [mdc/Data options]
   [:> mui/Dialog
    {:open (boolean open?)}
    (if title
      [:> mui/DialogTitle
       title])
    [:> mui/DialogContent
     "content here"]
    [:> mui/DialogActions
     [:> mui/Button
      {:on-click #(dispatch> cancel-event)}
      "Cancel"]]]])


(defn MenuDialog! []
  [MenuDialog (<subscribe [:material-desktop/menu-dialog])])
