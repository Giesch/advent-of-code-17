(ns advent-of-code.day-10-test
  (:require [advent-of-code.day-10 :refer :all]
            [clojure.test :refer :all]))

(deftest advent-10-1-test
  (is (= (advent-10-1 [0 1 2 3 4]
                      [3 4 1 5])
         12)))

(deftest advent-10-2-test
  (is (= (advent-10-2 "")
         "a2582a3a0e66e6e86e3812dcb672a272"))
  (is (= (advent-10-2 "AoC 2017")
         "33efeb34ea91902bb2f59c9920caa6cd"))
  (is (= (advent-10-2 "1,2,3")
         "3efbe78a8d82f29979031a4aa0b16a9d"))
  (is (= (advent-10-2 "1,2,4")
         "63960835bcdc130f0b66d7ff4f6a5a8e")))
