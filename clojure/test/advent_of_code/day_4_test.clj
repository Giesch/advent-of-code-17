(ns advent-of-code.day-4-test
  (:require [advent-of-code.day-4 :refer :all]
            [clojure.test :refer :all]))

(deftest advent-4-1-test
  (is (= (advent-4-1 "aa bb\naa cc\naa aa") 2)))

(deftest advent-4-2-test
  (is (= (advent-4-2 "aa bb\naa cc\nab ba") 2)))

