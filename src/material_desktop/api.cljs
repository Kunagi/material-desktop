(ns material-desktop.api
  (:require
   [clojure.spec.alpha :as s]
   [re-frame.core :as rf]
   [conform.api :refer [validate]]))

(s/def ::subscription-id    qualified-keyword?)
(s/def ::subscription-args  map?)
(s/def ::subscription       (s/cat :id   ::subscription-id
                                   :args (s/? ::subscription-args)))

(defn subscribe
  [subscription]
  (validate ::subscribe
            [:val subscription ::subscription])
  (rf/subscribe subscription))


(defn <subscribe
  [subscription]
  (if-let [signal (subscribe subscription)]
    @signal))
