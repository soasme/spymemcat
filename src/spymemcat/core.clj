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
  (get [this key])
  (gets [this key])

  ; Storage commands (6)
  (set [this key value]
    "store this data")
  (add [this key value]
    "store this data, but only if the server *doesn't* already hold value for this key")
  (replace [this key value]
    "store this data, but only if the server *does* already hold data for this key")
  (append [this key value]
    "add this data to an existing key after existing data")
  (prepend [this key value]
    "add this data to an existing key before existing data")
  (cas [this key value]
    "store this data but only if no one else has updated since I last fetched it.")

  ; Deletion
  (delete [this key])

  ; Increment/Decrement
  (incr [this key step])
  (decr [this key step])

  ; Touch
  (touch [this key exptime]
    "update the expiration time of an existing item without fetching it")

  ; Statistic
  (stats [this]
    "query the server about statistics it maintains and other internal data.")

  ; Others
  (version [this])
  (flush_all [this])
  (quit [this])
  )

(def ^:dynamic *memcached-client* nil)
(def no-client-error
  (Exception. "Please put commands under `with-client`."))

(defmacro with-client
  "Evalute body in the context of a thread-bound client to a memcached server."
  [client & body]
  `(binding [*memcached-client* ~client]
     ~@body))

(defn client
  "Return current thread-bound memcached client."
  []
  (deref (or *memcached-client*
             (throw no-client-error))))

(defn get
  ([key]
   (-> (client)
       (get key)))
  ([key & rest]
   (-> (client)
       (get (list* key rest)))))


