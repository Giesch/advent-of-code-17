(ns advent-of-code.core
  (:require [clojure.string :as string]))

;;;; Helpers

(defn- parse-digit [c]
  (Character/digit c 10))

(defn- parse-int [s]
  (Integer/parseInt s 10))

(defn- tab-split [s]
  (string/split s #"\t"))

;;;; Day 1

(defn- sum-matching-pairs [pairs]
  (->> pairs
       (filter (fn [[a b]] (= a b)))
       (map #(parse-digit (first %)))
       (reduce +)))

;;; Part 1

(defn advent-1-1
  "Sums all numbers in a circular string that are immediately repeated."
  [s]
  (->> s
       (partition 2 1 s)
       (sum-matching-pairs)))

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

;;;; Day 2

(defn- parse-apply-and-sum [f s]
  (->> s
       (string/split-lines)
       (map tab-split)
       (map #(map parse-int %))
       (map f)
       (reduce +)))

;;; Part 1

(defn advent-2-1
  "Sums the differences between the min and max of each line
    in a 'spreadsheet'"
  [s]
  (if (empty? s)
    0
    (parse-apply-and-sum #(- (apply max %) (apply min %)) s)))

;;; Part 2

(defn- find-quotient [nums]
  (first (for [x nums
               y nums
               :when (and (> x y)
                          (zero? (mod x y)))]
           (/ x y))))

(defn advent-2-2
  "Sums the integer quotients found on each line.
    Assumes there is exactly one per line"
  [s]
  (parse-apply-and-sum find-quotient s))
