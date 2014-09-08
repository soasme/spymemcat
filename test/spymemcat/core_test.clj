(ns spymemcat.core-test
  (:require [clojure.test :refer :all]
            [spymemcat.core :refer :all :as spy]
            )
  (:import java.util.UUID))

(defn gen-key [] (str (UUID/randomUUID)))

(defn test-client-fixture
  [f]
  (do ; H...
    (with-client (client-factory "localhost:11211")
      (try
        (f)
        (finally (spy/flush))))))

(clojure.test/use-fixtures :once test-client-fixture)

(deftest set-get
  (testing "Simple Set and then Get."
     (spy/set "set-get" 1 3600)
     (is (= 1 (spy/get "set-get")))))

(deftest test-store-get-delete
  (testing "Store / Get / Delete cmds"
    (let [key (str (gensym))]
      (is (spy/add key 1 3600))
      (is (not (deref (add key 2 3600))))
      (is (= 1 (get key)))
      (is (deref (replace key 2 3600)))
      (is (= 2 (get key)))
      (is (deref (set key 3 3600)))
      (is (= 3 (get key)))
      (is (deref (delete key)))
      (is (nil? (get key))))))

(deftest test-incr-decr
  (testing "a case when a value is not initialized and does not exist"
    (is (= -1 (incr (gen-key) 1))))
  (testing "a case when a value initialized and does not exist"
    (is (= 1 (incr (gen-key) 1 1))))
  )
