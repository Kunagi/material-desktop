(ns material-desktop.desktop.components.form-dialog
  (:require
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   ["@material-ui/core/colors" :as mui-colors]
   [re-frame.core :as rf]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]))


(defn TextField [field]
  [:div
   [:> mui/TextField field]])


(def field-type->component
  {:email TextField
   :text TextField})


(defn conform-field [field on-submit-event on-change-event]
  (-> field
      (assoc :on-change #(dispatch> on-change-event
                                    {:key (:key field)
                                     :value (-> % .-target .-value)}))
      (assoc :on-key-down #(when (= 13 (-> % .-keyCode))
                              (dispatch> on-submit-event)))
      (assoc :variant :filled)
      (assoc :margin :dense)
      (assoc :full-width true)))


;; TODO auto auto-focus first field
(defn Form [form submit-event field-value-change-event]
  (let [form-id "some-form-id"
        form-fields (:fields form)]
    [:div.Form
     (into [:div]
           (mapv (fn [{:as field :keys [type] :or {type :text}}]
                   [(field-type->component type)
                    (conform-field field submit-event field-value-change-event)])
                 form-fields))]))


(defn FormDialog [{:as options
                   :keys [open?
                          title
                          form
                          submit-text
                          cancel-event
                          submit-event
                          field-value-change-event]
                   :or {submit-text "Submit"}}]
  [:div
   [mdc/Data options]
   [:> mui/Dialog
    {:open (boolean open?)}
    (if title
      [:> mui/DialogTitle
       title])
    [:> mui/DialogContent
     [mdc/Data form]
     [Form form submit-event field-value-change-event]]
    [:> mui/DialogActions
     [:> mui/Button
      {:on-click #(dispatch> cancel-event)}
      "Cancel"]
     [:> mui/Button
      {:on-click #(dispatch> submit-event)
       :color :primary}
      submit-text]]]])


(defn FormDialog! []
  [FormDialog (<subscribe [:material-desktop/form-dialog])])
