# protected-eval

Application REPL Interface - select functions evaluable with nREPL, hide everything else.

![protected-eval](https://github.com/stacksideflow/protected-eval/raw/master/sample.gif)

### usage

Simply use `defnremote` instead of `defn` if you wish to expose your function to the remote nREPL user. Default policy is to drop every message received through REPL, except for evaluation of `defnremote`'d functions.


#### defnremote - accessible via remote nREPL
```clojure
(defnremote defnremoted-send-message [from to message]
  (send-message-impl from to message)
  (str "message sent, thanks for using our REPL API!"))
```
#### everything else - inaccessible via remote nREPL
```clojure
(defn normal-hidden-by-default []
  (str "any code that is not defnremote'd, try to find me (you can't)"))
```

### installation
[![Clojars Project](https://img.shields.io/clojars/v/protected-eval.svg)](https://clojars.org/protected-eval)
1. Add this to your project.clj

    ```clojure
    [protected-eval "0.1.2"]
    ```
2. [Default nREPL] Add this to your project.clj
    ```clojure
    :repl-options {:nrepl-middleware [protected-eval.core/eval-apply-remote-only]}
    ```
    or

     [Emacs+CIDER] Add this to your project.clj (allows to inspect classpath + some additional commands, less restrictive)*
   ```clojure
    :repl-options {:nrepl-middleware [protected-eval.core/eval-apply-remote-only-cider]}
    ```

   Start with `lein repl :headless`.
   After connecting to this nREPL instance, you will be able to access `defnremote`s only.

   If you launch an nREPL server with `lein repl` (non-headless), you have to use `protected-eval.core/eval-apply-remote-only-non-headless` and `protected-eval.core/eval-apply-remote-only-non-headless-cider` versions of the middleware.

    You have to require your namespace with remote functions somewhere
    (if you're using an isolated ns) for your functions to be resolved.

    *Emacs+CIDER `eval-apply-remote-only-cider` middleware description:

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
