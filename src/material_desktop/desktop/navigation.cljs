(ns material-desktop.desktop.navigation
  (:require
   [clojure.string :as string]
   [re-frame.core :as rf]
   [accountant.core :as accountant]
   [cemerick.url :as url]

   [bindscript.api :refer [def-bindscript]]))


(defn construct-page-path [page-key page-args]
  (str "/ui/"
       (namespace page-key) "/" (name page-key)
       (when-not (empty? page-args)
         (reduce
          (fn [path [k v]]
            (str path
                 (if (= "" path)  "?" "&")
                 (js/encodeURIComponent (name k))
                 "="
                 (js/encodeURIComponent v)))
          ""
          page-args))))


(def-bindscript ::construct-page-path
  path (construct-page-path :some/page {:with 1 :args 2}))


(defn activate-page [db page-key args]
  (tap> [::activate-page page-key args])
  (-> db
      (assoc-in [:material-desktop/desktop :current-page] page-key)
      (update-in [:material-desktop/desktop :pages page-key :args] merge args)))


(defn activate-page-from-url [db]
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
    (tap> [::activate-page-from-url page-key args])
    (activate-page db page-key args)))


(defn navigate! [page-key page-args]
  (accountant/navigate! (construct-page-path page-key page-args))
  ::navigate!)


(defn navigate!-with-db [db page-key page-args]
  (navigate! page-key page-args)
  db)


(defn integrate!
  []
  (tap> [::integrate-accountant])
  (accountant/configure-navigation!
   {:nav-handler (fn [path]
                   (rf/dispatch [:material-desktop/desktop.page-switched
                                 {:path path}]))
    :path-exists? (fn [path]
                    (.startsWith path "/ui/"))})
  (accountant/dispatch-current!))


