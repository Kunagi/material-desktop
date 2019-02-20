(ns material-desktop.api
  (:require
   [re-frame.core :as rf]))


(defn subscribe
  ([subscription-name]
   (subscribe subscription-name {}))
  ([subscription-name subscription-args]
   (if-not (qualified-keyword? subscription-name)
     (throw (ex-info "Subscription name needs to be a qualified keyword."
                     {:subscription-name subscription-name
                      :subscription-args subscription-args})))
   (if-not (or (nil? subscription-args) (map? subscription-args))
     (throw (ex-info "Subscription args need to be a map."
                     {:subscription-name subscription-name
                      :subscription-args subscription-args})))
   (rf/subscribe [subscription-name subscription-args])))


(defn <subscribe
  ([subscription-name]
   (<subscribe subscription-name {}))
  ([subscription-name subscription-args]
   (if-let [signal (subscribe subscription-name subscription-args)]
     @signal)))
