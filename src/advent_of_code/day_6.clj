(ns advent-of-code.day-6
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

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
  (first
   (apply max-key second
          (map-indexed vector v))))

(defn redistribute-max
  [v]
  (redistribute v (max-index v)))

(defn cached-redistribute-max
  "Given a set s and vector v, adds v to s and redistributes max 'box' in v."
  [[s v]]
  [(conj s v) (redistribute-max v)])

(defn redistributions
  [v]
  (iterate cached-redistribute-max [#{} v]))

(defn indexed
  [coll]
  (map-indexed vector coll))

(defn repeated? [[_ [s v]]] (s v))

(defn how-long
  [v]
  (first
   (first
    (filter repeated?
            (indexed (redistributions v))))))

(defn prepare-input [s]
  (->> s
       (tab-split)
       (map str->int)
       (vec)))

(def input
  "5	1	10	0	1	7	13	14	3	12	8	10	7	12	0	6")

;;; give up and use loop-recur

(defn give-up
  [v]
  (loop [v v
         s #{}
         i 0]
    (if (s v)
      i
      (recur (redistribute-max v)
             (conj s v)
             (inc i)))))
