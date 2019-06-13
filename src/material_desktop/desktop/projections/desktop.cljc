(ns material-desktop.desktop.projections.desktop
  (:require
   [bindscript.api :refer [def-bindscript]]
   [facts-db.api :as db]
   [facts-db.ddapi :as ddapi :refer [def-event def-query def-api events> <query new-db]]
   [conform.api :as conform]))


;;; helpers

(defn page-by-name
  [db page-name]
  (-> db
      (db/fact "desktop" :pages)
      (->> (map #(get db %))
           (filter #(= page-name (:name %)))
           (first))))


;;; constructor

(def-api ::desktop.projection.desktop
  :db-constructor
  (fn [{:keys [id]}]
    [{:db/id "desktop"
      :pages #{"home-page"}
      :current-page "home-page"
      :form-dialog "form-dialog"}
     {:db/id "home-page"
      :args {}}
     {:db/id "form-dialog"
      :open? false}]))


;;; events

(def-event ::page-switched
  (fn [db {:keys [page-name page-args]}]
    (let [page (page-by-name db page-name)
          page-id (:db/id page)]
      [{:db/id "desktop"
        :current-page page-id}
       {:db/id page-id
        :args page-args}])))


(def-event ::form-dialog-opened
  (fn [db {:as options :keys [submit-event]}]
    [(merge options
            {:db/id "form-dialog"
             :open? true
             :submit-callback-event submit-event})]))


(def-event ::form-dialog-closed
  (fn [db _]
    [{:db/id "form-dialog"
      :open? false}]))




;; (rf/reg-event-db
;;  :material-desktop/desktop.form-dialog.field-value-changed
;;  (fn [db [_ {:keys [key value]}]]
;;    (assoc-in db [:material-desktop/desktop :form-dialog :form :values key] value)))


;; (rf/reg-event-db
;;  :material-desktop/desktop.form-dialog.submitted
;;  (fn [db _]
;;    (let [values (get-in db [:material-desktop/desktop :form-dialog :form :values])
;;          submit-event (get-in db [:material-desktop/desktop :form-dialog :submit-callback-event])
;;          submit-event (update submit-event 1 assoc :values values)]
;;      (rf/dispatch submit-event))
;;    (desktop/close-form-dialog db)))


;; (rf/reg-event-db
;;  :material-desktop/desktop.form-dialog.canceled
;;  (fn [db _]
;;    (desktop/close-form-dialog db)))
