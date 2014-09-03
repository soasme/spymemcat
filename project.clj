(defproject spymemcat "0.1.0-SNAPSHOT"
  :description "A Clojure client for memcached"
  :url "http://github.com/soasme/spymemcat"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [spy/spymemcached "2.8.10"]]
  :repositories {"spy-memcached" {:url "http://files.couchbase.com/maven2/"}})
