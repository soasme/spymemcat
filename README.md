# spymemcat

A Clojure memcached client library wraps spymemcached.

## Lein Usage

To use spymemcat, put dependency to `project.clj`:

    [spymemcat "0.1.0"]


## Basic Usage

### Namespace

```clojure
(use 'spymemcat.core)
```

### Client

A memcached client using text protocol is default.

```clojure
(def memcached-client (client-factory "localhost:11211"))
```

All valid commands should under `with-client` scope:

```clojure
(with-client memcached-client)
  (set "test" 1 3600)
  (get "test"))
```

### Store commands

```clojure
(with-client memcached-client
  (set "set-key1" "value" 3600)
  (add "add-key1" "value" 3600)
  (replace "replace-key" "value" 3600)
  (touch "touch-key" 3600))
```

### Get commands

```clojure
(with-client memcached-client
  (get "key1") ;= 1
  (gets "key2") ;= {:cas 1 :value 1}
  (get-multi ["key1" "key2"]) ;= {"key1" 1 "key2" 1}
  )
```

### Delete command

```clojure
(with-client memcached-client
  (delete "key"))
```

## License

Copyright © 2014 Lin Ju (@soasme).

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
