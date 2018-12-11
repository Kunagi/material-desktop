(ns material-desktop.init
  (:require
   [clojure.spec.alpha :as s]))

(goog-define DEV false)

(.log js/console "Initializing Material Desktop" {"DEV" DEV})

(enable-console-print!)

(set! *assert* DEV)
(s/check-asserts DEV)


(defn install-roboto-css []
  (let [head (.-head js/document)
        link (.createElement js/document "link")]
    (set! (.-type link) "text/css")
    (set! (.-rel link) "stylesheet")
    (set! (.-href link) "https://fonts.googleapis.com/css?family=Roboto:300,400,500")
    (.appendChild head link)))
