(defproject winter-water "0.1.0-SNAPSHOT"
  :description "Winter Water - A 7/8 time composition using Leipzig and Overtone"
  :url "https://github.com/ctford/winter-water"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [leipzig/leipzig "0.10.0"]
                 [overtone "0.16.3331"]
                 [net.java.dev.jna/jna "5.15.0"]]
  :jvm-opts ["--add-opens" "java.desktop/com.apple.eawt=ALL-UNNAMED"])
