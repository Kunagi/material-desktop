(ns material-desktop.ddapi-integration
  (:require
   [re-frame.core :as rf]
   [facts-db.ddapi :as ddapi]))


(defn api-db-path
  [api-id db-instance-identifier]
  [:ddapi/dbs api-id db-instance-identifier])


(defn- reg-db-sub
  [api-id db-instance-identifier-args-key db-subscription-id]
  (tap> [::reg-sub db-subscription-id])
  (rf/reg-sub
   db-subscription-id
   (fn [db [_ {:as subscription-args :keys []}]]
     (let [db-instance-identifier (if db-instance-identifier-args-key
                                    (get subscription-args
                                         db-instance-identifier-args-key)
                                    :singleton)]
       (get-in db (api-db-path api-id db-instance-identifier))))))


(defn- reg-query-sub
  [query api-id db-subscription-id db-instance-identifier-args-key]
  (let [query-id (:id query)]
    (rf/reg-sub
     query-id
     (fn [[_ args]]
       (rf/subscribe [db-subscription-id args]))
     (fn [api-db [_ args]]
       (if-not api-db
         nil
         (let [args (dissoc args db-instance-identifier-args-key)]
           (ddapi/<query api-db [query-id args])))))))


(defn- reg-event
  [event api-id db-instance-identifier-args-key]
  (let [event-id (:id event)]
    (rf/reg-event-db
     event-id
     (fn [db [_ args]]
       (let [args (or args {})
             db-instance-identifier (if db-instance-identifier-args-key
                                      (get args
                                           db-instance-identifier-args-key)
                                      :singleton)
             path (api-db-path api-id db-instance-identifier)
             args (dissoc args db-instance-identifier-args-key)
             api-db (get-in db path)]
         ;; TODO autocreate
         (when-not api-db
           (throw (ex-info (str "No api-db in app-db at path " path ".") {})))
         (assoc-in db path (ddapi/events> api-db [[event-id args]])))))))



(defn integrate-ddapis-into-re-frame
  []
  (doseq [api (ddapi/defined-apis)]
    (let [api-id (:id api)
          db-instance-identifier-args-key (:db-instance-identifier-args-key api)
          db-subscription-id (keyword (name api-id) "db")
          queries (vals (:queries api))
          events (vals (:events api))]
      (when (:autocreate-singleton-db? api)
        (rf/dispatch [:ddapi/create-db-if-missing {:api-id api-id
                                                   :db-instance-identifier :singleton
                                                   :db-args {}}]))
      (reg-db-sub api-id db-instance-identifier-args-key db-subscription-id)
      (doseq [query queries]
        (reg-query-sub query api-id db-subscription-id db-instance-identifier-args-key))
      (doseq [event events]
        (reg-event event api-id db-instance-identifier-args-key)))))


(rf/reg-event-db
 :ddapi/create-db-if-missing
 (fn [db [_ {:keys [api-id db-instance-identifier db-args]}]]
   (let [api-db-path (api-db-path api-id db-instance-identifier)
         api-db (get-in db api-db-path)]
     (if api-db
       db
       (do
        (assoc-in db api-db-path (ddapi/new-db api-id db-args)))))))
