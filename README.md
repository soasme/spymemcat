# spymemcat

A Clojure memcached client library wraps spymemcached.

## Lein Usage

To use spymemcat, put dependency to `project.clj`:

    [spymemcat "0.1.0"]


## Basic Usage

```clojure
(with-client (client-factory "localhost:11211")
  (set "test" 1 3600)
  (get "test"))
```
## License

Copyright © 2014 Lin Ju (@soasme).

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
