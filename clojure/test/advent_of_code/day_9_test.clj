(ns advent-of-code.day-9-test
  (:require [advent-of-code.day-9 :refer :all]
            [clojure.test :refer :all]))

(deftest advent-9-1-test
  (is (= (advent-9-1 "{}")
         1))

  (is (= (advent-9-1 "{{{}}}")
         6))

  (is (= (advent-9-1 "{{},{}}")
         5))

  (is (= (advent-9-1 "{{{},{},{{}}}}")
         16))

  (is (= (advent-9-1 "{<a>,<a>,<a>,<a>}")
         1))

  (is (= (advent-9-1 "{{<ab>},{<ab>},{<ab>},{<ab>}}")
         9))

  (is (= (advent-9-1 "{{<!!>},{<!!>},{<!!>},{<!!>}}")
         9))

  (is (= (advent-9-1 "{{<a!>},{<a!>},{<a!>},{<ab>}}")
         3)))
