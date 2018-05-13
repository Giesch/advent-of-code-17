(ns advent-of-code.day-1-test
  (:require [advent-of-code.day-1 :refer :all]
            [clojure.test :refer :all]))

(deftest advent-1-1-test
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

(deftest advent-1-2-test
  (is (= (advent-1-2 "1234")
         0))
  (is (= (advent-1-2 "1221")
         0))
  (is (= (advent-1-2 "1212")
         6))
  (is (= (advent-1-2 "12131415")
         4)))
