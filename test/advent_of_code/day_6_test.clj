(ns advent-of-code.day-6-test
  (:require [advent-of-code.day-6 :refer :all]
            [clojure.test :refer :all]))

(deftest how-long-test
  (is (= (how-long [0 2 7 0]) 5)))

(deftest give-up-test
  (is (= (give-up [0 2 7 0]) 5)))
