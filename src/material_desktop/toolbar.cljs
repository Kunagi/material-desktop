(ns material-desktop.toolbar
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   [material-desktop.components :as mdc]))


(defn PaperToolbar [{:keys [title]} & components]
  [:> mui/Paper
   {:style {:background-color (-> mdc/palette :primary :main)
            :color "white"}}
   (cond-> [:> mui/Toolbar]

     title
     (conj
      [mdc/Text
       {:variant :h6
        :style {:color :inherit
                :flex-grow 1}}
       title])

     true
     (into components))])
