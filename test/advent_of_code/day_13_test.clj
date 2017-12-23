(ns advent-of-code.day-13-test
  (:require [advent-of-code.day-13 :refer :all]
            [clojure.test :refer :all]))

(let [example "0: 3
1: 2
4: 4
6: 4
"]

  (deftest advent-13-1-test
    (is (= (advent-13-1 example)
           24)))

  (deftest advent-13-2-test
    (is (= (advent-13-2 example)
           10))))
