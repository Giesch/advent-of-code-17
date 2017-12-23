(ns advent-of-code.day-12-test
  (:require [advent-of-code.day-12 :refer :all]
            [clojure.test :refer :all]))

(let [example "0 <-> 2
1 <-> 1
2 <-> 0, 3, 4
3 <-> 2, 4
4 <-> 2, 3, 6
5 <-> 6
6 <-> 4, 5
"]

  (deftest advent-12-1-test
    (is (= (advent-12-1 example)
           6)))

  (deftest advent-12-2-test
    (is (= (advent-12-2 example)
           2))))
