(ns spymemcat.core
  (:import [java.net Socket ServerSocket UnknownHostException]
           [java.io IOException DataInputStream DataOutputStream]
           [java.util.regex Pattern]
           [java.util.zip CRC32]
           [java.security NoSuchAlgorithmException MessageDigest]))

(defn- server-hash
  "Get hash for key"
  [key]
  (let [b (.getBytes key)
        c (doto (CRC32.)
            (.update b))]
    (.getValue c)))

; TODO
(defn socket-factory
  "Parse the connection string.

  Support:

  * unix://abolute/path
  * inet6://host:port
  * inet://host:port
  * host:port

  default port: 11211"
  [connection-string]
  (Socket. "localhost" 11211))

