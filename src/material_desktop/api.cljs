(ns material-desktop.api
  (:require
   [clojure.spec.alpha :as s]
   [re-frame.core :as rf]
   [conform.api :refer [validate]]))

(s/def ::subscription-id    qualified-keyword?)
(s/def ::subscription-args  map?)
(s/def ::subscription       (s/cat :id   ::subscription-id
                                   :args (s/? ::subscription-args)))

(s/def ::event-id    qualified-keyword?)
(s/def ::event-args  map?)
(s/def ::event       (s/cat :id   ::event-id
                                   :args (s/? ::event-args)))

(defn subscribe
  [subscription]
  (validate ::subscribe
            [:val subscription ::subscription])
  (rf/subscribe subscription))


(defn <subscribe
  [subscription]
  (if-let [signal (subscribe subscription)]
    @signal))


(defn dispatch>
  ([event additional-args]
   (validate ::dispatch>
             [:val event ::event]
             [:val additional-args ::event-args])
   (let [[event-id event-args] event
         event-args (or event-args {})
         event-args (merge event-args additional-args)]
     (dispatch> [event-id event-args])))
  ([event]
   (validate ::dispatch>
             [:val event ::event])
   (rf/dispatch event)))



