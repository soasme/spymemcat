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

(defn client-factory
  [& connection-strings])

(defn send-cmd
  "Sending cmd[1] to memcached via Socket.

  [1]: See memcached protocol."
  [client cmd])

(declare error-string)
(declare client-error-string)
(declare server-error-string)

(defprotocol Memcached
  ; Retrieval commands (2)
  (defn get [this key])
  (defn gets [this key])

  ; Storage commands (6)
  (defn set [this key value]
    "store this data")
  (defn add [this key value]
    "store this data, but only if the server *doesn't* already hold value for this key")
  (defn replace [this key value]
    "store this data, but only if the server *does* already hold data for this key")
  (defn append [this key value]
    "add this data to an existing key after existing data")
  (defn prepend [this key value]
    "add this data to an existing key before existing data")
  (defn cas [this key value]
    "store this data but only if no one else has updated since I last fetched it.")

  ; Deletion
  (defn delete [this key])

  ; Increment/Decrement
  (defn incr [this key step])
  (defn decr [this key step])

  ; Touch
  (defn touch [this key exptime]
    "update the expiration time of an existing item without fetching it")
  )
