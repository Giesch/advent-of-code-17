(ns advent-of-code.day-3)

;;;; Part 1

(defn- side-distances [lower]
  (let [upper (* 2 lower)]
    (concat (range (dec upper) lower -1)
            (range lower (inc upper) 1))))

(defn- square-distances [n]
  (apply concat (repeat 4 (side-distances n))))

(def ^:private distances
  (apply concat
         '(0) (map square-distances (iterate inc 1))))

(defn advent-3-1
  "Finds the manhattan distance to the center of a spiral-indexed square."
  [n]
  (last (take n distances)))

;;;; Part 2
