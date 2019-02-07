(ns ^:figwheel-hooks material-desktop.showcase
  (:require
   ["@material-ui/icons" :as icons]
   [reagent.core :as r]
   [re-frame.core :as rf]

   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop :as desktop]
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
               {}
               {}
               {}
               {}
               {}]}]]

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
    [mdc/Exception (ex-info "catched and thrown ex-info"
                            {:with :data}
                            "something failed")]]
   [Show
    {:title "ExceptionCard"}
    [mdc/ExceptionCard (ex-info "catched and thrown ex-info"
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
  (rf/dispatch-sync [::init])
  (mount-app))


(defn ^:after-load on-figwheel-after-load []
  (rf/dispatch-sync [::init])
  (mount-app))


(rf/reg-event-db
 ::init
 (fn [db _]
   (-> db
       (assoc-in [::desktop/desktop :appbar :title] "Material Desktop Showcase")
       (assoc-in [::desktop/desktop :workarea :components :showcase] [[showcase]]))))


(start)
