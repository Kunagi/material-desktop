(ns material-desktop.components
  (:require
   [cljs.pprint :as pprint]
   [reagent.core :as r]
   ["@material-ui/core" :as mui]
   ["@material-ui/icons" :as icons]
   ["@material-ui/core/colors" :as mui-colors]
   [goog.object :as gobj]

   [material-desktop.api :refer [<subscribe]]))


;;; color palette

(def palette
  {:primary {:main (gobj/get (.-blueGrey mui-colors) 700)}
   :secondary {:main (gobj/get (.-green mui-colors) 700)}
   :text-color (gobj/get (.-red mui-colors) 700)

   :greyed "#aaa"})

(def spacing-base 8)

(defn spacing [factor]
  (str (* factor spacing-base) "px"))


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


;;; ---


(defn Spacer
  ([size]
   (Spacer size size))
  ([width height]
   [:div.Spacer
    {:style {:width (spacing width)
             :height (spacing height)}}]))

;;; data


(defn Data
  [data]
  [:code
   {:style {:white-space :pre-wrap
            :overflow :auto}}
   (with-out-str (pprint/pprint data))])

;;; progress boundary


(defn DataProgressBoundary [component-f data]
  (if data
    [component-f data]
    [:> mui/CircularProgress]))


(defn SubscriptionProgressBoundary [component-f subscription]
  [DataProgressBoundary component-f (<subscribe subscription)])


;;; paper

(defn Paper [options & components]
  (into [:> mui/Paper
            options]
        components))


;;; toolbar



;;; text

(defn Subtitle [& elements]
  (with-options mui/Typography {:variant :subtitle1} elements))

(defn Text [& elements]
  (with-options mui/Typography {} elements))

;; (defn Caption [& elements]
;;   (with-options mui/Typography {:variant :caption} elements))

(defn Overline [& elements]
  (with-options mui/Typography {:variant :overline} elements))


(defn- Double-_ [tag text-1 text-2]
  [tag
   [:span {:style {:font-weight 700}}  text-1  " "]
   [:span {:style {:font-weight 100}}  "| "    text-2]])

(defn Double-H1 [text-1 text-2] (Double-_ :h1 text-1 text-2))
(defn Double-H2 [text-1 text-2] (Double-_ :h2 text-1 text-2))
(defn Double-H3 [text-1 text-2] (Double-_ :h3 text-1 text-2))
(defn Double-H4 [text-1 text-2] (Double-_ :h4 text-1 text-2))
(defn Double-H5 [text-1 text-2] (Double-_ :h5 text-1 text-2))
(defn Double-DIV [text-1 text-2] (Double-_ :div text-1 text-2))



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
       {:component-did-catch (fn [this cause info]
                               (.error js/console
                                       "ErrorBoundary"
                                       "\nthis:" this
                                       "\ne:" cause
                                       "\ninfo:" info)
                               (let [stack (.-componentStack info)
                                     message (if stack
                                               (.trim (str stack))
                                               (str info))]
                                 (reset! !exception (ex-info message
                                                             {:component comp}
                                                             cause))))
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


;;; buttons

(defn button-options [options]
  (cond-> {:variant :contained
           :on-click (if-let [href (:href options)]
                       #(set! (.-location js/window) href)
                       (:on-click options))}

          (:full-width? options)
          (assoc-in [:style :width] "100%")))


(defn Button
  [& {:as options :keys [text]}]
  [:> mui/Button
   (button-options options)
   text])


(defn ButtonsColumn
  [& {:as options :keys [buttons
                         title]}]
  (into [:div
         {:style {:display :flex
                  :flex-direction :column}}
         (if title
           [:div
            {:style {:color "#455a64"
                     :text-transform :uppercase
                     :font-size "90%"
                     :border-bottom "2px solid #455a64"
                     :margin-bottom "0.5rem"}}
            title])]
            ;; {:stlye {:border-bottom "1px solid grey"
            ;;          :border "1px solid red"}}
            ;; (Overline title)])]
        (mapv (fn [button]
                [:div
                 {:style {:padding "4px 0"}}
                 (into [Button
                        :full-width? true]
                       (apply concat button))])
              buttons)))


;; (defn FloatButton
;;   [options & contents]
;;   [:> mui/Fab [:> icons/Add]])


(defn IconButton
  [options & contents]
  (into [:> mui/IconButton (merge {} options)]
        contents))


;;; layouts


(defn Columns
  [& components]
  (into [:div
         {:style {:display :flex
                  :margin "0 -0.5rem"}}]
        (mapv (fn [column]
                [:div
                 {:style {:margin "0 0.5rem"}}
                 column])
              components)))


;;; cards

(defn Card [& args]
  [ErrorBoundary (into [:> mui/Card] args)])

(defn- CardWrapper [& card-args]
  [:div
   {:style {:margin-bottom "0.5rem"}}
   (-> [Card]
       (into card-args))])


(defn CardContent [& args]
  (into [:> mui/CardContent] args))


(defn CardsColumn [& {:keys [cards]}]
  (-> [:div.CardsColumn]
      (into (map (fn [card] [CardWrapper card])) cards)))


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
