(ns material-desktop.expansion-panel-list
  (:require
   [reagent.core :as r]
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]))

;;; ExpansionPanel

(defn ExpansionPanel [panel-model]
  [:> mui/ExpansionPanel
   [:> mui/ExpansionPanelSummary
    {:expand-icon (r/as-element [:> icons/ExpandMore])}
    [:div
     {:style {:font-weight 500}}
     (get-in panel-model [:summary :text])]]
   [:> mui/ExpansionPanelDetails
    [(get-in panel-model [:details :component]) panel-model]]])

(defn ExpansionPanelList [list-model]
  (into [:div]
        (mapv (fn [panel-model]
                [ExpansionPanel panel-model])
              (:panels list-model))))
