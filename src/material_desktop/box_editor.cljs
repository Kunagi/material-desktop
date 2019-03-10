(ns material-desktop.box-editor
  (:refer-clojure :exclude [Box])
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]

   [material-desktop.components :as mdc]))


(def box-spacing "2px")

(defn flatten-children [children]
  (reduce
   (fn [ret child]
     (if (vector? child)
       (conj ret child)
       (into ret child)))
   []
   children))

(defn Box [{:keys [type-label
                   label]}
           & inner-boxes]
  [:> mui/Paper
   {:style {:padding (mdc/spacing 1)}}
   [:div
    [:span
     {:style {:color "#999"}}
     type-label]
    " "
    [:span
     {:style {:font-weight 500}}
     label]]
   (if-not (empty? inner-boxes)
     (into [:div
            {:style {:display :flex
                     :flex-direction :column
                     :align-items :flex-start
                     :padding-top (str "-" box-spacing)
                     :padding-bottom (str "-" box-spacing)}}
            [mdc/Spacer 1]]
           (map (fn [box]
                  [:div
                   {:style {:padding-top box-spacing
                            :padding-bottom box-spacing}}
                   box])
                (flatten-children inner-boxes))))])

(defn BoxContainer
  [options root-box]
  [:div.BoxContainer
   {:style {:display :flex}}
   root-box])
