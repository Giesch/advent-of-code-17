(ns advent-of-code.day-6
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(def input
  "5	1	10	0	1	7	13	14	3	12	8	10	7	12	0	6")

(defn prepare-input [s]
  (->> s
       (tab-split)
       (map str->int)
       (vec)))

;;;; Part 1

(defn distribute
  "Distributes n boxes in vector v, beginning at index i."
  [v n i]
  (loop [v v
         n n
         i i]
    (if (zero? n)
      v
      (recur (update v
                     (mod i (count v))
                     inc)
             (dec n)
             (inc i)))))

(defn redistribute
  [v i]
  (distribute (assoc v i 0)
              (get v i)
              (inc i)))

(defn max-index
  [v]
  (let [m (apply max v)]
    (first
     (some #(if (= (second %) m)
              %
              nil)
           (indexed v)))))

(defn redistribute-max
  [v]
  (redistribute v (max-index v)))

(defn how-long
  [v]
  (loop [v v
         s #{}
         i 0]
    (if (s v)
      i
      (recur (redistribute-max v)
             (conj s v)
             (inc i)))))

;;;; Part 2
