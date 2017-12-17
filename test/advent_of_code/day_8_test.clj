(ns advent-of-code.day-8-test
  (:require [advent-of-code.day-8 :refer :all]
            [clojure.test :refer :all]))

(let [example   "b inc 5 if a > 1
a inc 1 if b < 5
c dec -10 if a >= 1
c inc -20 if c == 10
  "]
  (deftest advent-8-1-test
    (is (= (advent-8-1 example)
           1)))

  (deftest advent-8-2-test
    (is (= (advent-8-2 example)
           10))))
