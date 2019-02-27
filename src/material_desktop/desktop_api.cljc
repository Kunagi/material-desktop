(ns material-desktop.desktop-api
  (:require
   [bindscript.api :refer [def-bindscript]]
   [facts-db.api :as db]
   [facts-db.ddapi :as ddapi :refer [def-event def-query def-api events> <query new-db]]))


(def-api :desktop
  :autocreate-singleton-db? true
  :db-constructor
  (fn [db args]
    (-> db
        (db/++ [
                {:db/id :form-dialog
                 :open? false
                 :form-query nil}]))))


(def-event :desktop/form-dialog-triggered
  (fn [db {:keys [form-query]}]
    (db/++ db [{:db/id :form-dialog
                :open? true
                :form-query form-query}])))


(def-event :desktop/form-dialog-input-changed
  (fn [db {:keys [field-id value]}]
    (db/++ db {:db/id :form-dialog
               :values (assoc (db/fact db :form-dialog :values) field-id value)})))


(def-event :desktop/form-dialog-closed
  (fn [db {:keys []}]
    (db/++ db [{:db/id :form-dialog
                :open? false
                :form-query nil}])))


(def-query :desktop/form-dialog
  (fn [db _]
    (db/tree db :form-dialog {})))


(def-query :desktop/form-dialog-values
  (fn [db _]
    (db/fact db :form-dialog :values)))
