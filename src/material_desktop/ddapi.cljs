(ns material-desktop.ddapi
  (:require
   [clojure.spec.alpha :as s]
   [re-frame.core :as rf]))


(defn path-to-ddapi-db
  [api-id instance-identifier]
  (let [instance-identifier (or instance-identifier :singleton)]
    [:material-desktop/ddapi-dbs api-id instance-identifier]))



(defn <query
  ([query]
   (<query query nil nil))
  ([query args]
   (<query query nil args))
  ([query db-instance-identifier args]
   (let [api-id (keyword (namespace query))
         signal (rf/subscribe [:material-desktop/ddapi-db
                               {:api-id api-id
                                :db-instance-identifier db-instance-identifier}])]
     (if signal
       (let [db @signal
             query-f (get-in db [:db/config :<query])]
         (query-f db query args))))))




(rf/reg-sub
 :material-desktop/ddapi-db
 (fn [db [_ {:keys [api-id db-instance-identifier]}]]
   (get-in db (path-to-ddapi-db api-id db-instance-identifier))))


