# protected-eval

### defnremote - accessible via remote nREPL
```clojure
(defnremote eval-me-with-nrepl []
  (str "some useful info about the server, like current time: "
       (quot (System/currentTimeMillis) 1000)))
```
### defn - inaccessible via remote nREPL
```clojure
(defn do-not-eval-me-with-nrepl []
  (str "some very sensitive info about the server, like OS version: "
       (System/getProperty "os.version")))

```

## License

GNU GPL v3
