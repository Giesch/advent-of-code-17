(ns advent-of-code.day-2-test
  (:require [advent-of-code.day-2 :refer :all]
            [clojure.test :refer :all]))

(deftest advent-2-1-test
  (is (= (advent-2-1 "1\t2")
         1))
  (is (= (advent-2-1 "1\t2\n0\t5")
         6))
  (is (= (advent-2-1 "1")
         0))
  (is (= (advent-2-1 "")
         0)))

(deftest advent-2-2-test
  (is (= (advent-2-2 "5\t9\t2\t8\n9\t4\t7\t3\n3\t8\t6\t5")
         9)))
