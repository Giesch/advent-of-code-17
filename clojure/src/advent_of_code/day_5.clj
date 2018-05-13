(ns advent-of-code.day-5
  (:require [clojure.string :as string]
            [advent-of-code.core :refer :all]))

;;;; Part 1

(defn jump [[v i j]]
  (if (get v i)
    (let [i' (+ i (get v i))
          v' (update v i inc)]
      [v' i' (inc j)])
    [nil nil (inc j)]))

(defn jumping [v]
  (iterate jump [v 0 0]))

(defn prepare-input [s]
  (vec (map str->int
            (string/split-lines s))))

(defn advent-5-1 [s]
  (let [v (prepare-input s)]
    (last
     (last
      (take-while #(second %) (jumping v))))))

;;;; Part 2

(defn nonsense [offset]
  (if (>= offset 3)
    (dec offset)
    (inc offset)))

(defn new-jump [[v i j]]
  (if (get v i)
    (let [i' (+ i (get v i))
          v' (update v i nonsense)]
      [v' i' (inc j)])
    [nil nil (inc j)]))

(defn new-jumping [v]
  (iterate new-jump [v 0 0]))

(defn advent-5-2 [s]
  (let [v (prepare-input s)]
    (last
     (last
      (take-while #(second %) (new-jumping v))))))
