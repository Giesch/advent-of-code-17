(ns advent-of-code.day-3
  (:require [advent-of-code.core :refer :all]))

;;;; Part 1

(defn side-distances [lower]
  (let [upper (* 2 lower)]
    (concat (range (dec upper) lower -1)
            (range lower (inc upper) 1))))

(defn square-distances [n]
  (apply concat (repeat 4 (side-distances n))))

(def distances
  (apply concat
         '(0)
         (map square-distances
              (iterate inc 1))))

(defn advent-3-1
  "Finds the manhattan distance to the center of a spiral-indexed square."
  [n]
  (last (take n distances)))

;;;; Part 2

(def offsets
  {:north [0 1]
   :east  [1 0]
   :south [0 -1]
   :west  [-1 0]})

(def to-the-left
  {:north :west
   :east  :north
   :south :east
   :west  :south})

(def initial-state
  {:grid {[0 0] 1},
   :facing :south,
   :location [0 0]})

(defn add-pairs [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn look-dir [loc dir]
  (add-pairs loc (dir offsets)))

(defn look-left
  [{:keys [grid facing location]}]
  (get grid
       (look-dir location (facing to-the-left))
       nil))

(defn choose-direction
  [{:keys [facing] :as state}]
  (if (look-left state)
    facing
    (facing to-the-left)))

(defn neighbors [loc]
  (map (partial add-pairs loc)
       (for [x [-1 0 1]
             y [-1 0 1]
             :when (not (and (zero? x) (zero? y)))]
         [x y])))

(defn sum-neighbors [grid loc]
  (apply + (map #(get grid % 0)
                (neighbors loc))))

(defn step
  [{:keys [grid facing location] :as state}]
  (let [dir      (choose-direction state)
        next-loc (look-dir location dir)
        v        (sum-neighbors grid next-loc)]
    {:grid     (assoc grid next-loc v),
     :facing   dir,
     :location next-loc}))

(defn lookup [{:keys [grid location]}]
  (get grid location))

(defn advent-3-2 [n]
  (lookup (find-first #(< n (lookup %))
                      (iterate step initial-state))))
