(ns advent-of-code.day-2
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn- apply-to-line [f s]
  (->> s
       (tab-split)
       (map str->int)
       (f)))

(defn- parse-apply-and-sum [f s]
  (->> s
       (string/split-lines)
       (map #(apply-to-line f %))
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
