(ns advent-of-code.day-1
  (:require [advent-of-code.core :refer :all]))

(defn sum-matching-pairs [pairs]
  (->> pairs
       (filter (fn [[a b]] (= a b)))
       (map #(char->int (first %)))
       (reduce +)))

;;; Part 1

(defn advent-1-1
  "Sums all numbers in a circular string that are immediately repeated."
  [s]
  (sum-matching-pairs (partition 2 1 s s)))

;;; Part 2

(defn opposite-pairs [s]
  (let [c (count s)]
    (->> s
         (cycle)
         (partition (inc (/ c 2)) 1)
         (take c)
         (map (fn [coll] [(first coll) (last coll)])))))

(defn advent-1-2 [s]
  "Sums all numbers in a circular string
      that are repeated 'opposite' the circle.
      Assumes the string has an even length."
  (sum-matching-pairs (opposite-pairs s)))

