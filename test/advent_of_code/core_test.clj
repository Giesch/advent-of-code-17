(ns advent-of-code.core-test
  (:require [clojure.test :refer :all]
            [advent-of-code.core :refer :all]))

(deftest advent-1-1-tests
  (is (= (advent-1-1 "123")
         0))
  (is (= (advent-1-1 "1233")
         3))
  (is (= (advent-1-1 "11")
         2))
  (is (= (advent-1-1 "1")
         1))
  (is (= (advent-1-1 "")
         0)))

(deftest advent-1-2-tests
  (is (= (advent-1-2 "1234")
         0))
  (is (= (advent-1-2 "1221")
         0))
  (is (= (advent-1-2 "1212")
         6))
  (is (= (advent-1-2 "12131415")
         4)))

(deftest advent-2-1-tests
  (is (= (advent-2-1 "1\t2")
         1))
  (is (= (advent-2-1 "1\t2\n0\t5")
         6))
  (is (= (advent-2-1 "1")
         0))
  (is (= (advent-2-1 "")
         0)))

(deftest advent-2-2-tests
  (is (= (advent-2-2 "5\t9\t2\t8\n9\t4\t7\t3\n3\t8\t6\t5")
         9)))
