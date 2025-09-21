(defproject winter-water "0.1.0-SNAPSHOT"
  :description "Winter Water - A 7/8 time composition using Leipzig and Overtone"
  :url "https://github.com/ctford/winter-water"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [leipzig/leipzig "0.10.0"]
                 [overtone "0.10.6"]]
  :main ^:skip-aot winter-water.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})