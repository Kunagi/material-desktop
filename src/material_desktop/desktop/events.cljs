(ns material-desktop.desktop.events
  (:require
   [re-frame.core :as rf]
   [accountant.core :as accountant]

   [bindscript.api :refer [def-bindscript]]
   [material-desktop.desktop.api :as desktop]))


(defn construct-page-path [page-key page-args]
  (str "/ui/"
       (namespace page-key) "/" (name page-key)
       (when-not (empty? page-args)
         (reduce
          (fn [path [k v]]
            (str path
                 (if (= "" path)  "?" "&")
                 (js/encodeURIComponent (name k))
                 "="
                 (js/encodeURIComponent v)))
          ""
          page-args))))


(def-bindscript ::construct-page-path
  path (construct-page-path :some/page {:with 1 :args 2}))


(rf/reg-event-db
 :material-desktop/activate-page
 (fn [db [_ {:keys [page-key page-args]}]]
   ;; (desktop/activate-page db page-key page-args)))
   ;;(tap> [::activate-page page-key page-args (construct-page-path page-key page-args)])
   (accountant/navigate! (construct-page-path page-key page-args))
   db))


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
