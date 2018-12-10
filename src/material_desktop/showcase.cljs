(ns ^:figwheel-hooks material-desktop.showcase
  (:require
   [cljsjs.material-ui]
   [reagent.core :as r]
   [re-frame.core :as rf]

   [material-desktop.init :as init]
   [material-desktop.components :as mdc]
   [material-desktop.desktop :as desktop]))


(defn show-frame [options component]
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

   [show-frame
    {:title "edn"}
    [mdc/edn {:key-1 "string" :key-2 [1 2 3]}]]

   [show-frame
    {:title "text-body1"}
    [mdc/text-body1 "Lorem Ipsum"]]

   [show-frame
    {:title "text-body2"}
    [mdc/text-body2 "Lorem Ipsum"]]

   [show-frame
    {:title "text-caption"}
    [mdc/text-caption "Lorem Ipsum"]]

   [show-frame
    {:title "text-title"}
    [mdc/text-title "Lorem Ipsum"]]

   [show-frame
    {:title "text-headline"}
    [mdc/text-headline "Lorem Ipsum"]]

   [show-frame
    {:title "text-subheading"}
    [mdc/text-subheading "Lorem Ipsum"]]

   [show-frame
    {:title "card"}
    [mdc/card "card text"]]

   [show-frame
    {:title "tabs-paper"}
    [mdc/tabs-paper {:tabs [{:label "tab-1"
                             :content "content of tab-1"}
                            {:label "tab-2"
                             :content "content of tab-2"}]}]]

   [show-frame
    {:title "form"}
    [mdc/form (r/atom {:fields [{:name :name
                                 :label "Name"}
                                {:name :email
                                 :label "E-Mail"}]
                       :vals {:name "Witek"}})]]

   [show-frame
    {:title "exception-div"}
    [mdc/exception-div (ex-info "catched and thrown ex-info"
                                {:with :data}
                                "something failed")]]
   [show-frame
    {:title "exception-card"}
    [mdc/exception-card (ex-info "catched and thrown ex-info"
                                 {:with :data}
                                 "something failed")]]])


(defn root-ui []
  [mdc/error-boundary
   [desktop/desktop]])


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
