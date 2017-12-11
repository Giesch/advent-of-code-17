(ns advent-of-code.day-7-test
  (:require [advent-of-code.day-7 :refer :all]
            [clojure.test :refer :all]))

(deftest find-root-test
  (is (= (find-root "pbga (66)
xhth (57)
ebii (61)
havc (66)
ktlj (57)
fwft (72) -> ktlj, cntj, xhth
qoyq (66)
padx (45) -> pbga, havc, qoyq
tknk (41) -> ugml, padx, fwft
jptl (61)
ugml (68) -> gyxo, ebii, jptl
gyxo (61)
cntj (57)
")
         "tknk"))
  )
