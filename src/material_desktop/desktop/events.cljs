(ns material-desktop.desktop.events
  (:require
   [re-frame.core :as rf]
   [accountant.core :as accountant]

   [material-desktop.desktop.api :as desktop]
   [material-desktop.desktop.navigation :as navigation]))


;;; navigation

(rf/reg-event-db
 :material-desktop/desktop.page-switch-requested
 (fn [db [_ {:keys [page-key page-args]}]]
   (tap> [::desktop.page-switch-requested page-key page-args])
   (navigation/navigate! page-key page-args)
   db))


(rf/reg-event-db
 :material-desktop/desktop.page-switched
 (fn [db [_ {:keys [path]}]]
     (navigation/activate-page-from-url db)))


;;; form-dialog


(rf/reg-event-db
 :material-desktop/desktop.form-dialog.field-value-changed
 (fn [db [_ {:keys [key value]}]]
   (assoc-in db [:material-desktop/desktop :form-dialog :form :values key] value)))


(rf/reg-event-db
 :material-desktop/desktop.form-dialog.submitted
 (fn [db _]
   (let [values (get-in db [:material-desktop/desktop :form-dialog :form :values])
         submit-event (get-in db [:material-desktop/desktop :form-dialog :submit-callback-event])
         submit-event (update submit-event 1 assoc :values values)]
     (rf/dispatch submit-event))
   (desktop/close-form-dialog db)))


(rf/reg-event-db
 :material-desktop/desktop.form-dialog.canceled
 (fn [db _]
   (desktop/close-form-dialog db)))
