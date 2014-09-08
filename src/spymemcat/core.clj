(ns spymemcat.core
  (:import [java.net Socket ServerSocket UnknownHostException]
           [java.io IOException DataInputStream DataOutputStream]
           [java.util.regex Pattern]
           [java.util.zip CRC32]
           [java.security NoSuchAlgorithmException MessageDigest]
           [java.net InetSocketAddress]
           [net.spy.memcached MemcachedClient BinaryConnectionFactory AddrUtil])
  (:require [clojure.string :as string]))

(defn- server-hash
  "Get hash for key"
  [key]
  (let [b (.getBytes key)
        c (doto (CRC32.)
            (.update b))]
    (.getValue c)))

(defn- cas-value-parser
  [cas-value]
  {:value (.getValue cas-value)
   :cas (.getCas cas-value)})

(defn- keywordize
  [v]
  (-> v
      str
      string/lower-case
      keyword))

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

; Retrieval commands

(defn get
  [key]
  (.get (client) key))

(defn gets
  [key]
  (-> (.gets (client) key)
      cas-value-parser))

(defn get-multi
  [coll]
  (into {} (.getBulk (client) coll)))

; Storage commands

(defmacro defstoragecmd
  [name]
  `(defn ~name
     [key# value# expire#]
     (. (client) ~name key# expire# value#)))

(defmacro defcountercmd
  [name]
  `(defn ~name
     ([key# delta#]
      (. (client) ~name key# delta#))
     ([key# delta# default#]
      (. (client) ~name key# delta# default#))
     ([key# delta# default# expire#]
      (. (client) ~name key# delta# default# expire#))))


(defstoragecmd set)
(defstoragecmd add)
(defstoragecmd replace)
(defstoragecmd touch)

(defn touch
  [key expire]
  (.touch (client) key expire))

(defcountercmd incr)
(defcountercmd decr)

(defn cas
  [key value cas-id]
   (keywordize (.cas client key cas-id value)))

(defn delete
  [key]
  (.delete (client) key))

(defn versions
  []
  (.getVersions (client)))

(defn stats
  ([]
   (.getStats (client))))

(defn shutdown
  []
  (.shutdown (client)))

(defn flush
  []
  (.flush (client)))
