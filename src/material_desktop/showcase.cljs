(ns ^:figwheel-hooks material-desktop.showcase
  (:require
   ["@material-ui/icons" :as icons]
   [reagent.core :as r]
   [re-frame.core :as rf]

   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop.components.desktop :as desktop]
   [material-desktop.expansion-panel-list :as expansion-panel-list]))


(defn Show [options component]
  (let [title (or (:title options) (str (first component)))]
    [:div
     {:style {:margin-top "10px"
              :color "#999"}}
     title
     [:div
      {:style {:background "#FFFFFF"
               :color "#333"}}
      component]]))


(defn showcase []
  [:div

   [Show
    {:title "ButtonsColumn"}
    [mdc/ButtonsColumn
     :buttons [{:text "Click me"}
               {:text "No, click me."}
               {:text "Not me"}]]]

   [Show
    {:title "ExpansionPanelList"}
    [expansion-panel-list/ExpansionPanelList
     {:panels [
               {:summary {:text "panel 1"}
                :details {:component [:div [:h3 "some"] "details"]}}
               {:summary {:text "panel 2"}
                :details {:component "details"}}
               {:summary {:text "panel 3"}
                :details {:component "details"}}
               {:summary {:text "panel 4"}
                :details {:component "details"}}]}]]

   [Show
    {:title "Data"}
    [mdc/Data {:key-1 "string" :key-2 [1 2 3]}]]

   [Show
    {:title "Subtitle"}
    [mdc/Subtitle "Lorem Ipsum"]]

   [Show
    {:title "Text"}
    [mdc/Text "Lorem Ipsum"]]

   [Show
    {:title "Overline"}
    [mdc/Overline "Lorem Ipsum"]]

   [Show
    {:title "Icon: Face"}
    [:> icons/Face]]

   [Show
    {:title "Card"}
    [mdc/Card
     [mdc/CardContent
      "card content"]]]

   [Show
    {:title "TabsPaper"}
    [mdc/TabsPaper {:tabs [{:label "tab-1"
                            :content "content of tab-1"}
                           {:label "tab-2"
                            :content "content of tab-2"}]}]]

   [Show
    {:title "Form"}
    [mdc/Form (r/atom {:fields [{:name :name
                                 :label "Name"}
                                {:name :email
                                 :label "E-Mail"}]
                       :vals {:name "Witek"}})]]

   [Show
    {:title "Exception"}
    [mdc/Exception (ex-info "catched an thrown ex-info"
                            {:with :data}
                            "something failed")]]
   [Show
    {:title "ExceptionCard"}
    [mdc/ExceptionCard (ex-info "catched an thrown ex-info"
                                {:with :data}
                                "something failed")]]])

(defn root-ui []
  [mdc/ErrorBoundary
   [desktop/Desktop]])


(defn mount-app []
  (r/render
   [root-ui]
   (js/document.getElementById "app")))


(defn ^:export start
  []
  (init/install-roboto-css)
  (mount-app))


(defn ^:after-load on-figwheel-after-load []
  (mount-app))
