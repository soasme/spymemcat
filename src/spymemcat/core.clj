(ns spymemcat.core
  (:import [java.net Socket ServerSocket UnknownHostException]
           [java.io IOException DataInputStream DataOutputStream]
           [java.util.regex Pattern]
           [java.util.zip CRC32]
           [java.security NoSuchAlgorithmException MessageDigest]
           [java.net InetSocketAddress]
           [net.spy.memcached MemcachedClient BinaryConnectionFactory AddrUtil])
  (:require [clojure.string :as str]))

(defn- server-hash
  "Get hash for key"
  [key]
  (let [b (.getBytes key)
        c (doto (CRC32.)
            (.update b))]
    (.getValue c)))

(defn client-factory
  "Split a string in the form of `host:port host2:port` into a List
  of InetSocketAddress instances suitable for instantiating a
  MemcachedClient. Note that colon-delimited IPv6 is also supported."
  [connection-strings]
  (delay (MemcachedClient.
   (BinaryConnectionFactory.)
   (AddrUtil/getAddresses connection-strings))))

(defn send-cmd
  "Sending cmd[1] to memcached via Socket.

  [1]: See memcached protocol."
  [client cmd])

(declare error-string)
(declare client-error-string)
(declare server-error-string)

(def ^:dynamic *memcached-client* nil)
(def no-client-error
  (Exception. "Please put commands under `with-client`."))

(defmacro with-client
  "Evalute body in the context of a thread-bound client to a memcached server."
  [client & body]
  `(binding [*memcached-client* ~client]
     ~@body))

(defn- client
  "Return current thread-bound memcached client."
  []
  (deref (or *memcached-client*
             (throw no-client-error))))

(defn get
  ([key]
   (.get (client) key))
  ([key & rest]
   (.get (client) (list* key rest))))

(defn- cas-value-parser
  [cas-value]
  {:value (.getValue cas-value)
   :cas (.getCas cas-value)})

(defn gets
  [key]
  (-> (.gets (client) key)
      cas-value-parser))

(defn get-multi
  [coll]
  (into {} (.getBulk (client) coll)))

(defn set
  ([key value expiration]
   (.set (client) key expiration value)))

 (with-client (client-factory "localhost:11211")
   (set "test" 1 3600)
   (get "test") ;= 1
   (gets "test") ;= {:value 1, :cas 54}
   (get-multi ["test"]) ;= {"test" 1}
   )
