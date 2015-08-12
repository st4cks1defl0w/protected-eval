(ns protected-eval.core-test
  (:require [clojure.test :refer :all]
            [protected-eval.core :refer :all]))

(defn send-message-impl [message from to])

(defnremote defnremoted-visible-function-send-message [from to message]
  (send-message-impl from to message)
  (str "message sent, but you can't access any other part of code!"))

(defn normal-code-hidden-by-default []
  (str "any code that is not defnremote'd, try to find me (you can't)"))

(deftest offline-basic
  (is (= (defnremoted-visible-function-send-message "nREPLguest" "admin" "hi")
         "message sent, but you can't access any other part of code!"))
  (is (= (normal-code-hidden-by-default)
         "any code that is not defnremote'd, try to find me (you can't)")))

(run-tests 'protected-eval.core-test)
