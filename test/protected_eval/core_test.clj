(ns protected-eval.core-test
  (:require [clojure.test :refer :all]
            [protected-eval.core :refer :all]))

(defn send-message-impl [message from to])

(defnremote defnremoted-send-message [from to message]
  (send-message-impl from to message)
  (str "message sent, thanks for using our REPL API!"))

(defn normal-hidden-by-default []
  (str "any code that is not defnremote'd, try to find me (you can't)"))

(deftest offline-basic
  (is (= (defnremoted-send-message "nREPLguest" "admin" "hi")
         "message sent, thanks for using our REPL API!"))
  (is (= (normal-hidden-by-default)
         "any code that is not defnremote'd, try to find me (you can't)")))

(run-tests 'protected-eval.core-test)
