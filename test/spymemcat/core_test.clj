(ns spymemcat.core-test
  (:require [clojure.test :refer :all]
            [spymemcat.core :refer :all :as spy]
            )
  (:import java.util.UUID))

(defn gen-key [] (str (UUID/randomUUID)))

(defn test-client-fixture
  [f]
  (with-client (client-factory "localhost:11211")
    (try
      (f)
      (finally (spy/flush)))))

(clojure.test/use-fixtures :once test-client-fixture)

(deftest test-set-get
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
  (testing "incr when a value is not initialized and does not exist"
    (is (= -1 (incr (gen-key) 1))))
  (testing "incr when a value is initialized and does not exist"
    (let [k (gen-key)]
      (is (= 1 (incr k 1 1)))
      (is (= 2 (incr k 1)))))
  (testing "decr when a value is not initialized and does not exist"
    (is (= -1 (decr (gen-key) 1))))
  (testing "decr when a value is initialized and does not exist"
    (let [k (gen-key)]
      (is (= 10 (decr k 1 10)))
      (is (= 9 (decr k 1))))))

(deftest test-version
  (testing "with the version command returns something"
    (-> (spy/versions) empty? not is)))

(deftest test-zero-expiration
  (testing "expire = 0"
    (let [k (gen-key)
          v "some-value"]
      (spy/set k v 0)
      (is (= v (spy/get k))))))

(deftest test-delete
  (testing "deleting"
    (let [k (gen-key)
          v "some-value"]
      (spy/set k v 3600)

      (is (= v (spy/get k)))
      (spy/delete k)
      (is (nil? (spy/get k))))))

(deftest test-delete
  (testing "flush all keys"
    (let [k (gen-key)
          v "some-value"]
      (spy/set k v 10)
      (is (= v (spy/get k)))
      (spy/flush)
      (is (nil? (spy/get k))))))

(deftest test-add
  (testing "add will fail if key exists"
    (let [k (gen-key)
          v "some-value"]
      (spy/set k v 10)
      (is (false? @(spy/add k v 10)))))
  (testing "add will success if key does not exist"
    (let [k (gen-key)
          v "some-value"]
      (is (true? @(spy/add k v 10)))
      (is (= v (spy/get k))))))

(deftest test-replace
  (testing "replace will fail if key does not exist"
    (let [k (gen-key)
          v "some-value"]
      (is (false? @(spy/replace k v 10)))))
  (testing "replace will success if key exists"
    (let [k (gen-key)
          v "some-value"]
      (spy/set k v 10)
      (is (true? @(spy/replace k "other-value" 10)))
      (is (= "other-value" (spy/get k))))))

(deftest test-get-multi
  (testing "Return a dict"
    (spy/set "k1" 1 10)
    (spy/set "k2" 2 10)
    (is (= {"k1" 1 "k2" 2} (spy/get-multi ["k1" "k2"])))))
