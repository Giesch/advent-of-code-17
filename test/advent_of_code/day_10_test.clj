(ns advent-of-code.day-10-test
  (:require [advent-of-code.day-10 :refer :all]
            [clojure.test :refer :all]))

(let [v [0 1 2 3 4]
      lengths [3 4 1 5]]

  (deftest advent-10-1-test
    (is (= (advent-10-1 v lengths)
           12))))
