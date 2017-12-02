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

(defn- apply-to-line [f s]
  (->> s
       (tab-split)
       (map parse-int)
       (f)))

;;; Part 1

(defn- find-difference [line]
  (apply-to-line #(- (apply max %) (apply min %))
                 line))

(defn- sum-line-differences [s]
  (->> s
       (string/split-lines)
       (map find-difference)
       (reduce +)))

(defn advent-2-1
  "Sums the differences between the min and max of each line
    in a 'spreadsheet'"
  [s]
  (if (empty? s)
    0
    (sum-line-differences s)))

;;; Part 2

(defn- find-and-divide [nums]
  (first (for [x nums
               y nums
               :when (and (> x y)
                          (zero? (mod x y)))]
           (/ x y))))

(defn advent-2-2
  "Sums the integer quotients found on each line.
    Assumes there is exactly one per line"
  [s]
  (->> s
       (string/split-lines)
       (map #(apply-to-line find-and-divide %))
       (reduce +)))
