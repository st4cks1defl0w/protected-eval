(defproject protected-eval "0.1.7"
  :description "Application REPL Interface -
                select functions evaluable with nREPL,
                hide everything else."
  :url "https://stacksideflow.host"
  :license {:name "GNU GPL v 3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :scm {:name "git" :url "https://github.com/stacksideflow/protected-eval"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [nrepl "0.4.0"]
                 [lein-cljfmt "0.6.4"]
                 [jonase/eastwood "0.3.5"]
                 [lein-bikeshed "0.5.1"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
