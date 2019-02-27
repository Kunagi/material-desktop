(ns material-desktop.editing
  (:require
   [reagent.core :as r]
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   ["@material-ui/core/colors" :as mui-colors]

   [material-desktop.api :refer [<subscribe dispatch>]]
   [material-desktop.components :as mdc]))


(defn TextField [field]
  [:div
   [:> mui/TextField field]])


(def field-type->component
  {:email TextField
   :text TextField})


(defn conform-field [field on-change-event on-submit-event]
  (-> field
      (assoc :on-change #(dispatch> on-change-event
                                    {:value (-> % .-target .-value)}))
      (assoc :on-key-down #(when (= 13 (-> % .-keyCode))
                              (dispatch> on-submit-event)
                              (dispatch> [:desktop/form-dialog-closed])))
      (assoc :margin :dense)
      (assoc :full-width true)))


(defn Form [form on-submit-event]
  (let [form-id "some-form-id"
        form-fields (:fields form)]
    [:div
     (into [:div]
           (mapv (fn [field]
                   [(field-type->component (:type field))
                    (conform-field field [:desktop/form-dialog-input-changed
                                          {:field-id (:id field)}]
                                         on-submit-event)])
                 form-fields))]))


(defn FormDialog [& {:keys [open?
                            form]}]
  (let [values (<subscribe [:desktop/form-dialog-values])
        on-submit-event (update (:submit-event form)
                                1
                                assoc
                                :values values)]
    [:> mui/Dialog
     {:open (boolean open?)}
     (if-let [title (:title form)]
       [:> mui/DialogTitle
        title])
     [:> mui/DialogContent
      [mdc/Data form]
      [Form form on-submit-event]]
     [:> mui/DialogActions
      [:> mui/Button
       {:on-click #(dispatch> [:desktop/form-dialog-closed])}
       "Cancel"]
      [:> mui/Button
       {:on-click (fn []
                    (dispatch> on-submit-event)
                    (dispatch> [:desktop/form-dialog-closed]))
        :color :primary}
       (or (:submit-text form) "Submit")]]]))


(defn TextFieldDialog [& {:keys [open?
                                 title
                                 text-field-options
                                 cancel-event
                                 submit-event
                                 submit-text]
                          :or {open? true
                               submit-text "Submit"}}]
  (FormDialog
   :open? open?
   :title title
   :cancel-event cancel-event
   :submit-event submit-event
   :submit-text submit-text
   :form {:fields [text-field-options]}))
