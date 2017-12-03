(ns advent-of-code.day-1
  (:require [advent-of-code.core :refer :all]))

(defn- sum-matching-pairs [pairs]
  (->> pairs
       (filter (fn [[a b]] (= a b)))
       (map #(char->digit (first %)))
       (reduce +)))

;;; Part 1

(defn advent-1-1
  "Sums all numbers in a circular string that are immediately repeated."
  [s]
  (sum-matching-pairs (partition 2 1 s s)))

;;; Part 2

(defn- first-half-pairs [s]
  (let [half (/ (count s) 2)]
    (->> s
         (partition (inc half) 1)
         (take half)
         (map (fn [coll] [(first coll) (last coll)])))))

(defn advent-1-2
  "Sums all numbers in a circular string
    that are repeated 'opposite' the circle.
    Assumes the string has an even length."
  [s]
  (->> s
       (first-half-pairs)
       (sum-matching-pairs)
       (* 2)))

