(ns protected-eval.core
  (:require [clojure.set]
            [nrepl.server :as server]
            [nrepl.middleware :refer [set-descriptor!]]))

(defonce
  ^{:doc "Keeping track of active REPL connections - will be useful for
         future full-fledged ARI authing."}
  *active-repls
  (atom #{}))

(defmacro defnremote [fn-name & fn-rest]
  `(defn ~(vary-meta fn-name assoc :remote-eval true) ~@fn-rest))

(defn- defnremote? [code]
  (->> code
       clojure.edn/read-string
       first
       symbol
       resolve
       meta
       :remote-eval))

(defn- defnremote-dbg? [code]
  (println "symbol->>>>>>>>"
           (->> code
                clojure.edn/read-string
                first
                symbol))
  (println "resolve->>>>>>>>"
           (->> code
                clojure.edn/read-string
                first
                symbol
                resolve))
  (println "meta->>>>>>"
           (->> code
                clojure.edn/read-string
                first
                symbol
                resolve
                meta))
  (->> code
       clojure.edn/read-string
       first
       symbol
       resolve
       meta
       :remote-eval))

(def ^:private whitelisted-ops
  #{"close" "interrupt"})

(def ^:private whitelisted-ops-cider
  (clojure.set/union whitelisted-ops #{"classpath" "clone" "describe" "eldoc"}))

(defn eval-apply-remote-only
  "nREPL middleware with DROP-default policy. Allows only
    evalutaion of defnremote'd functions, other evals are converted to nil."
  [h]
  (fn [{:keys [code op] :as msg}]
    (if (or (contains? whitelisted-ops op)
            (and (defnremote? code) (= op "eval")))
      (h msg)
      (h (assoc msg :op "eval" :code "nil")))))

(defn eval-apply-remote-only-cider-dbg
  "Same as eval-apply-remote-only, but works with Emacs
    CIDER REPL (eval in repl buffer)
    Less secure. Following nREPL operations are allowed
    (necessary for minimal CIDER repl connection):
    - classpath:
    return a sequence of File objects of elements on the classpath.
    - clone
    initiates (copies) a new session, nothing is disclosed
    - describe
    transmits a collection of available nREPL operations
    (lists all of them, disregarding nrepl-protected limitations)
    - close
    closes nREPL connection
    - interrupt
    interrupts nREPL connection "
  [h]
  (fn [{:keys [code op] :as msg}]
    (println "msg = " msg)
    (when op
      (defnremote-dbg? code))
    (if (or (contains? whitelisted-ops-cider op)
            (and (= op "eval") (defnremote? code)))
      (h msg)
      (h (assoc msg :op "eval" :code "nil")))))

(defn eval-apply-remote-only-cider
  "Same as eval-apply-remote-only, but works with Emacs
    CIDER REPL (eval in repl buffer)
    Less secure. Following nREPL operations are allowed
    (necessary for minimal CIDER repl connection):
    - classpath:
    return a sequence of File objects of elements on the classpath.
    - clone
    initiates (copies) a new session, nothing is disclosed
    - describe
    transmits a collection of available nREPL operations
    (lists all of them, disregarding nrepl-protected limitations)
    - close
    closes nREPL connection
    - interrupt
    interrupts nREPL connection "
  [h]
  (fn [{:keys [code op] :as msg}]
    (if (or (contains? whitelisted-ops-cider op)
            (and (defnremote? code) (= op "eval")))
      (h msg)
      (h (assoc msg :op "eval" :code "nil")))))

(defn eval-apply-remote-only-non-headless
  "Same as eval-apply-remote-only but compatible with <lein repl>,
  *active-repls count state is currently necessary to whitelist
    REPL startup calls"
  [h]
  (fn [{:keys [code op session] :as msg}]
    (when (some? session)
      (reset! *active-repls (conj @*active-repls session)))
    (if (or (<= (count @*active-repls) 2)
            (contains? whitelisted-ops op)
            (and (defnremote? code) (= op "eval")))
      (h msg)
      (h (assoc msg :op "eval" :code "nil")))))

(defn eval-apply-remote-only-non-headless-cider
  "Same as eval-apply-remote-only-cider but compatible with <lein repl>,
  *active-repls count state is currently necessary to whitelist
    REPL startup calls"
  [h]
  (fn [{:keys [code op session] :as msg}]
    (when (some? session)
      (reset! *active-repls (conj @*active-repls session)))
    (if (or (<= (count @*active-repls) 2)
            (contains? whitelisted-ops-cider op)
            (and (defnremote? code) (= op "eval")))
      (h msg)
      (h (assoc msg :op "eval" :code "nil")))))

(def ^:private protected-eval-middlewares
  #{#'eval-apply-remote-only
    #'eval-apply-remote-only-cider
    #'eval-apply-remote-only-non-headless
    #'eval-apply-remote-only-non-headless-cider})

(doseq [protected-eval-middleware protected-eval-middlewares]
  (set-descriptor! protected-eval-middleware
                   {:requires (set server/default-middlewares)}))
