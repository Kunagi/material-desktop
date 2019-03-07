(ns material-desktop.desktop.api)


(defn activate-page [db page-key args]
  (-> db
      (assoc-in [:material-desktop/desktop :current-page] page-key)
      (update-in [:material-desktop/desktop :pages page-key :args] merge args)))


(defn open-form-dialog [db & {:as options :keys [submit-event]}]
  (assoc-in db [:material-desktop/desktop :form-dialog]
            (merge options
                   {:submit-callback-event submit-event
                    :open? true
                    :field-value-change-event [:material-desktop/desktop.form-dialog.field-value-changed {}]
                    :cancel-event [:material-desktop/desktop.form-dialog.canceled]
                    :submit-event [:material-desktop/desktop.form-dialog.submitted]})))


(defn close-form-dialog [db]
  (assoc-in db [:material-desktop/desktop :form-dialog :open?] false))
