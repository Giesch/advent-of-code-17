(ns advent-of-code.day-19-test
  (:require [advent-of-code.day-19 :refer :all]
            [clojure.test :refer :all]))

(let [example
"     |          
     |  +--+    
     A  |  C    
 F---|----E|--+ 
     |  |  |  D 
     +B-+  +--+ 
"]

  (deftest advent-19-1-test
    (is (= "ABCDEF"
           (advent-19-1 example))))


  )
