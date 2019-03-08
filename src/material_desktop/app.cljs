(ns material-desktop.app
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [accountant.core :as accountant]
   [cemerick.url :as url]

   [material-desktop.init :as init]
   [material-desktop.desktop.api :as desktop]
   [material-desktop.ddapi-integration :as ddapi-integration]
   [material-desktop.components :as mdc]))




(defonce !ui-root-component (atom [:div "ui-root-component"]))



(defn mount-app []
  (r/render
   [mdc/ErrorBoundary
    @!ui-root-component]
   (js/document.getElementById "app")))


(defn reload []
  (ddapi-integration/integrate-ddapis-into-re-frame)
  (mount-app))


(defn integrate-accountant
  []
  (tap> [::integrate-accountant])
  (accountant/configure-navigation!
   {:nav-handler (fn [path]
                   (rf/dispatch [::nav {:path path}]))
    :path-exists? (fn [path]
                    (.startsWith path "/ui/"))})
  (accountant/dispatch-current!))


(defn start [ui-root-component]
  (reset! !ui-root-component ui-root-component)
  (init/install-roboto-css)
  (integrate-accountant)
  (ddapi-integration/integrate-ddapis-into-re-frame)
  (mount-app))


(rf/reg-event-db
 ::nav
 (fn [db [_ {:keys [path]}]]
   (let [url (-> js/window .-location .-href url/url)
         path (-> url :path (.substring 4))
         path (string/split path #"/")
         [page-ns page-name] path
         page-ns (or (if (= 0 (count page-ns))
                       nil
                       page-ns)
                     "app")
         page-name (or page-name "home")
         page-key (keyword page-ns page-name)
         args (-> url :query)]
     (tap> [::nav page-key args])
     (desktop/activate-page db page-key args))))
