(ns material-desktop.desktop.api)


;;; menu dialog

(defn open-menu-dialog [db & {:as options :keys []}]
  (assoc-in db [:material-desktop/desktop :menu-dialog]
            (merge options
                   {:open? true
                    :cancel-event [:material-desktop/desktop.menu-dialog.canceled]})))


(defn close-menu-dialog [db]
  (assoc-in db [:material-desktop/desktop :menu-dialog :open?] false))


;;; form dialog

(defn open-form-dialog [db & {:as options :keys [submit-event]}]
  (assoc-in db [:material-desktop/desktop :form-dialog]
            (merge options
                   {:submit-callback-event submit-event
                    :open? true})))


(defn close-form-dialog [db]
  (assoc-in db [:material-desktop/desktop :form-dialog :open?] false))
