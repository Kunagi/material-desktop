(ns material-desktop.components
  (:require
   [cljs.pprint :as pprint]
   [reagent.core :as r]
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]))

;;; utils

(defn- deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (when (some identity vs)
      (reduce #(rec-merge %1 %2) v vs))))

(defn- with-options [component additional-options children]
  (let [existing-options (first children)]
    (if (map? existing-options)
      (into [:> component (deep-merge existing-options additional-options)]
            (rest children))
      (into [:> component additional-options]
            children))))


;;; data


(defn Data
  [data]
  [:code
   {:style {:white-space :pre-wrap
            :overflow :auto}}
   (with-out-str (pprint/pprint data))])


;;; text

(defn Subtitle [& elements]
  (with-options mui/Typography {:variant :subtitle1} elements))

(defn Text [& elements]
  (with-options mui/Typography {} elements))

;; (defn Caption [& elements]
;;   (with-options mui/Typography {:variant :caption} elements))

(defn Overline [& elements]
  (with-options mui/Typography {:variant :overline} elements))

;;; exception

(defn Exception [exception]
  (let [message (.-message exception)
        message (if message message (str exception))
        data (ex-data exception)
        cause (or (ex-cause exception) (.-cause exception))]
    [:div
     (if cause
       [:div
        [Exception cause]
        [Text
         {:style {:margin-top "1em"}}
         "Consequence:"]])
     [Subtitle
      {:style {:white-space :pre-wrap}}
      (str message)]
     (if-not (empty? data)
       [Data data])]))


(defn ExceptionCard [exception]
  [:> mui/Card
   {:style {:background-color "#FFCDD2"}}
   [:> mui/CardContent
    [:> icons/BugReport
     {:style {:float :left}}]
    (Subtitle "A bug is making trouble...")
    (if exception
      [:div
       {:style {:margin-top "1em"}}
       [Exception exception]])]])


(defn ErrorBoundary [comp]
  (if comp
    (let [!exception (r/atom nil)]
      (r/create-class
       {:component-did-catch (fn [cause info]
                               (.error js/console "ErrorBoundary" cause info)
                               (reset! !exception (ex-info (.trim (str (.-componentStack info)))
                                                           {:component comp}
                                                           cause)))
        :reagent-render (fn [comp]
                          (if-let [exception @!exception]
                            [ExceptionCard exception]
                            comp))}))))


;;; form


(defn- form-field
  [{:as field-model
    :keys [value on-change auto-focus name label type]}
   !form-state]
  [:div
   {:style {:margin "0.5em 0"}}
   [:> mui/TextField
    {:type type
     :default-value (get-in @!form-state [:vals name])
     ;; :on-change #(on-change (-> % .-target .-value))
     :on-change #(swap! !form-state
                        assoc-in [:vals name ] (-> % .-target .-value))
     :on-key-press #(when (= 13 (-> % .-charCode))
                      ((:on-submit @!form-state) @!form-state))
     :name name
     :label label
     :auto-focus auto-focus}]])


(defn Form
  [!form-state]
  (fn [!form-state]
    (let [form-state @!form-state
          fields (assoc-in (:fields form-state) [0 :auto-focus] true)]
      [:div
       ;; [edn model]
       ;; [:hr]
       (into [:div
              {:style {:display :flex
                       :flex-direction :column
                       :margin "-0.5em"}}]
             (map #(form-field % !form-state)
                  fields))])))


(defn FormDialog
  [{:as model
    :keys [title waiting? error-message on-cancel on-submit]}]
  (let [!form-state (r/atom model)]
    [:> mui/Dialog
     {:open true}
     [:> mui/DialogTitle title]
     [:> mui/DialogContent
      (if waiting?
        [:div
         [:> mui/CircularProgress]]
        [:div
         [Form !form-state]
         (if error-message
           [Text
            {:style {:color "red"
                     :margin-bottom "1em"}}
            error-message])])]
     [:> mui/DialogActions
      [:> mui/Button
       {:on-click on-cancel}
       "Cancel"]
      [:> mui/Button
       {:on-click #(on-submit @!form-state)
        :disabled waiting?}
       "Sign In"]]]))


;;; cards

(defn Card [& args]
  [ErrorBoundary (into [:> mui/Card] args)])


;;; tabs

(defn TabsPaper
  [options]
  (let [!tab-index (r/atom (or (:tab-index options) 0))]
    (fn [options]
      [:> mui/Paper
       options
       [:div
        (into [:> mui/Tabs
               {:value @!tab-index
                :on-change #(reset! !tab-index %2)}]
              (map (fn [tab] [:> mui/Tab {:label (:label tab)}]) (:tabs options)))
        [:div
         {:style {:padding "1em"}}
         (:content (nth (:tabs options) @!tab-index))]]])))
