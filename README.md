# protected-eval

### usage

Simply use `defnremote` instead of `defn` if you wish to expose your function to be evalable with nREPL. Default policy is to drop every message received through REPL, except for evaluation of `defnremote`'d functions.


#### defnremote - accessible via remote nREPL
```clojure
(defnremote eval-me-with-nrepl []
  (str "some useful info about the server, like current time: "
       (quot (System/currentTimeMillis) 1000)))
```
#### defn (and all other code) - inaccessible via remote nREPL
```clojure
(defn do-not-eval-me-with-nrepl []
  (str "some very sensitive info about the server, like OS version: "
       (System/getProperty "os.version")))
```

### installation
1. Add this to your project.clj

    ```clojure
    [protected-eval "0.1.1"]
    ```
2. [Default nREPL] Add this to your project.clj
    ```clojure
    :repl-options {:nrepl-middleware [protected-eval.core/eval-apply-remote-only]}
    ```
    or

     [Emacs+CIDER] Add this to your project.clj (allows to inspect classpath + some additional commands, less restrictive)
   ```clojure
    :repl-options {:nrepl-middleware [protected-eval.core/eval-apply-remote-only-cider]}
    ```
    Emacs+CIDER `eval-apply-remote-only-cider` middleware description:

     "Same as eval-apply-remote-only, but works with
    Emacs CIDER REPL (eval in repl buffer)
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
  interrupts nREPL connection"

## License

GNU GPL v3
