(ns material-desktop.desktop.subs
  (:require
   [re-frame.core :as rf]))


(rf/reg-sub
 :material-desktop/current-page
 (fn [db [_ {:keys [default-page-key]}]]
   (let [key (get-in db [:material-desktop/desktop :current-page])
         key (or key default-page-key)
         args (get-in db [:material-desktop/desktop :pages key :args])]
     {:key key
      :args args})))


(rf/reg-sub
 :material-desktop/current-page-key
 (fn [db _]
   (get-in db [:material-desktop/desktop :current-page :key])))


(rf/reg-sub
 :material-desktop/current-page-args
 (fn [db _]
   (get-in db [:material-desktop/desktop :current-page :args])))


(rf/reg-sub
 :material-desktop/form-dialog
 (fn [db _]
   (get-in db [:material-desktop/desktop :form-dialog])))
