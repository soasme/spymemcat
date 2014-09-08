(ns spymemcat.core-test
  (:require [clojure.test :refer :all]
            [spymemcat.core :refer :all :as spy]
            ))

(defn test-client-fixture
  [f]
  (do ; H...
    (with-client (client-factory "localhost:11211")
      (f))))

(clojure.test/use-fixtures :once test-client-fixture)

(deftest set-get
  (testing "Simple Set and then Get."
     (spy/set "set-get" 1 3600)
     (is (= 1 (spy/get "set-get")))))
