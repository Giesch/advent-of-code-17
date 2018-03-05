(ns advent-of-code.day-19
  (:require [clojure.string :as string]
            [advent-of-code.core :refer :all]))

(defn prepare-input [s]
  (->> s
     (string/split-lines)
     (map vec)))

(defn initial-state [location]
  {:location location
   :direction :down
   :letters []})

(defn evaluate [vs]

  )

(defn advent-19-1 [s]
  (-> s
     (prepare-input)
     (evaluate)))

(comment
  (def example
    "     |          
     |  +--+    
     A  |  C    
  F---|----E|--+ 
     |  |  |  D 
     +B-+  +--+ 
  ")

  (prepare-input example))

