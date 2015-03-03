(defproject protected-eval "0.1.0"
  :description "  "
  :url "https://stacksideflow.host"
  :license {:name "GNU GPU v 3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [lein-cljfmt "0.6.4"]
                 [jonase/eastwood "0.3.5"]
                 [lein-bikeshed "0.5.1"]]
  :main ^:skip-aot protected-eval.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
