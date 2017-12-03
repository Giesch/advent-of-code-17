(ns advent-of-code.day-3-test
  (:require [advent-of-code.day-3 :refer :all]
            [clojure.test :refer :all]))

(deftest advent-3-1-test
  (is (= (advent-3-1 1)
         0))
  (is (= (advent-3-1 12)
         3))
  (is (= (advent-3-1 1024)
         31)))
