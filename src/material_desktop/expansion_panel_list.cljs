(ns material-desktop.expansion-panel-list
  (:require
   [reagent.core :as r]
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]))

;;; ExpansionPanel

;; TODO & args
(defn ExpansionPanel
  [{:as panel-model :keys [summary details]}]
  [:> mui/ExpansionPanel
   [:> mui/ExpansionPanelSummary
    {:expand-icon (r/as-element [:> icons/ExpandMore])}
    [:div
     {:style {:font-weight 500}}
     (:text summary)]]
   [:> mui/ExpansionPanelDetails
    (or [(:component details) details]
        [:div "ExpansionPanel: [:details :component] missing"])]])

;; TODO & args
(defn ExpansionPanelList
  [& {:as options :keys [panels]}]
  (into [:div]
        (mapv (fn [panel-model]
                [ExpansionPanel panel-model])
              panels)))
