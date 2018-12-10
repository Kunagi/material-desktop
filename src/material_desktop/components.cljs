(ns material-desktop.components
  (:require
   [cljs.pprint :as pprint]
   [reagent.core :as r]
   [cljsjs.material-ui]
   [cljs-react-material-ui.reagent :as mui]
   [cljs-react-material-ui.icons :as icon]))


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
      (into [component (deep-merge existing-options additional-options)]
            (rest children))
      (into [component additional-options]
            children))))


;;; data


(defn edn
  [data]
  [:code
   {:style {:white-space :pre-wrap
            :overflow :auto}}
   (with-out-str (pprint/pprint data))])


;;; text

(defn text-body1 [& elements]
  (with-options mui/typography {:variant :body1} elements))

(defn text-body2 [& elements]
  (with-options mui/typography {:variant :body2} elements))

(defn text-caption [& elements]
  (with-options mui/typography {:variant :caption} elements))

(defn text-title [& elements]
  (with-options mui/typography
    {:variant :title
     :style {:font-size "95%"}}
    elements))

(defn text-headline [& elements]
  (with-options mui/typography {:variant :headline} elements))

(defn text-subheading [& elements]
  (with-options mui/typography {:variant :subheading} elements))


;;; exception

(defn exception-div [exception]
  (let [message (.-message exception)
        message (if message message (str exception))
        data (ex-data exception)
        cause (or (ex-cause exception) (.-cause exception))]
    [:div
     (if cause
       [:div
        [exception-div cause]
        [text-caption
         {:style {:margin-top "1em"}}
         "Consequence:"]])
     [text-subheading
      {:style {:white-space :pre-wrap}}
      (str message)]
     (if-not (empty? data)
       [edn data])]))


(defn exception-card [exception]
  [mui/card
   {:style {:background-color "#FFCDD2"}}
   [mui/card-content
    [icon/bug-report
     {:style {:float :left}}]
    (text-title "A bug is making trouble...")
    (if exception
      [:div
       {:style {:margin-top "1em"}}
       [exception-div exception]])]])


(defn error-boundary [comp]
  (if comp
    (let [!exception (r/atom nil)]
      (r/create-class
       {:component-did-catch (fn [cause info]
                               (.error js/console "error-boundary" cause info)
                               (reset! !exception (ex-info (.trim (str (.-componentStack info)))
                                                           {:component comp}
                                                           cause)))
        :reagent-render (fn [comp]
                          (if-let [exception @!exception]
                            [exception-card exception]
                            comp))}))))


;;; form


(defn- form-field
  [{:as field-model
    :keys [value on-change auto-focus name label type]}
   !form-state]
  [:div
   {:style {:margin "0.5em 0"}}
   [mui/text-field
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


(defn form
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


(defn form-dialog
  [{:as model
    :keys [title waiting? error-message on-cancel on-submit]}]
  (let [!form-state (r/atom model)]
    [mui/dialog
     {:open true}
     [mui/dialog-title title]
     [mui/dialog-content
      (if waiting?
        [:div
         [mui/circular-progress]]
        [:div
         [form !form-state]
         (if error-message
           [text-body1
            {:style {:color "red"
                     :margin-bottom "1em"}}
            error-message])])]
     [mui/dialog-actions
      [mui/button
       {:on-click on-cancel}
       "Cancel"]
      [mui/button
       {:on-click #(on-submit @!form-state)
        :disabled waiting?}
       "Sign In"]]]))


;;; cards

(defn card [& args]
  [error-boundary (into [mui/card] args)])


;;; tabs

(defn tabs-paper
  [options]
  (let [!tab-index (r/atom (or (:tab-index options) 0))]
    (fn [options]
      [mui/paper
       options
       [:div
        (into [mui/tabs
               {:value @!tab-index
                :on-change #(reset! !tab-index %2)}]
              (map (fn [tab] [mui/tab {:label (:label tab)}]) (:tabs options)))
        [:div
         {:style {:padding "1em"}}
         (:content (nth (:tabs options) @!tab-index))]]])))
