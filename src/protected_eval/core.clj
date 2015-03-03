(ns spachat.protected-eval)

(defonce
  ^{:doc "Keeping track of active REPL connections - will be useful for
         future full-fledged ARI authing."}
  *active-repls
  (atom #{}))

(defmacro defnremote [name & rest]
    `(defn ~(vary-meta name assoc :remote-eval true) ~@rest))

(defn- defnremote? [code]
  (->> code
       clojure.edn/read-string
       first
       symbol
       resolve
       meta
       :remote-eval))

(defn eval-apply-remote-only
  "nREPL middleware with DROP-default policy. Allows only
   evalutaion of defnremote'd functions, other evals are converted to nil.
   *active-repls count state is currently necessary to whitelist
   nREPL internal startup calls"
  [h]
  (fn [{:keys [code op session] :as msg}]
    (when (some? session)
        (reset! *active-repls (conj @*active-repls session)))
    (if (or (>= 2 (count @*active-repls))
            (and (= op "eval") (defnremote? code)))
      (h msg)
      (h (assoc msg :code "nil")))))

(def ^:private
  whitelisted-ops-cider
  #{"classpath" "clone" "describe" "eldoc" "close" "interrupt"})

(defn eval-apply-remote-only-cider
  "Same as eval-apply-remote-only, but works with Emacs CIDER REPL (eval in repl buffer)
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
  (fn [{:keys [code op session] :as msg}]
    (when (some? session)
      (reset! *active-repls (conj @*active-repls session)))
    (if (or (contains? whitelisted-ops-cider op)
            (>= 2 (count @*active-repls))
            (and (= op "eval") (defnremote? code)))
      (h msg)
      (h (assoc msg :code "nil")))))

(defnremote eval-me-with-nrepl []
  (str "some useful info about the server, like current time: "
           (quot (System/currentTimeMillis) 1000)))

(defn do-not-eval-me-with-nrepl []
  (str "some very sensitive info about the server, like OS version: "
           (System/getProperty "os.version")))
