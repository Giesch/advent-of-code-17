(ns advent-of-code.day-6-test
  (:require [advent-of-code.day-6 :refer :all]
            [clojure.test :refer :all]))

(deftest how-long-test
  (is (= (how-long [0 2 7 0]) 5))
  (is (= (how-long [5 1 10 0 1 7 13 14 3 12 8 10 7 12 0 6]) 5042)))

(deftest loop-size-test
  (is (= (loop-size [0 2 7 0]) 4))
  (is (= (loop-size [5 1 10 0 1 7 13 14 3 12 8 10 7 12 0 6]) 1086)))
