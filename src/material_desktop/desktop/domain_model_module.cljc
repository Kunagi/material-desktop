(ns material-desktop.desktop.domain-model-module
  (:require
   [material-desktop.desktop.projections.desktop]))

(def events
  [

   [:event-created
    {:id "id-event-page-switched"
     :ident :page-switched}]

   [:projection-created
    {:id "id-projection-desktop"
     :ident :desktop}]

   [:projection-event-handler-created
    {:projection-id "id-projection-desktop"
     :event-id "id-event-page-switched"}]])
