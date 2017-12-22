(ns advent-of-code.day-10
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn read-length [v i l]
  (->> v
       (cycle)
       (drop i)
       (take l)))

(defn write-length [v i length]
  (let [l (count length)
        doubled (concat v v)
        draft (concat (take i doubled)
                      length
                      (drop (+ i l)
                            doubled))]
    (concat
     (take i
           (drop (count v)
                 draft))
     (take (- (count v) i)
           (drop i draft)))))

(defn reverse-and-write [v i l]
  (write-length v i (reverse (read-length v i l))))

(defn update-state
  "Takes a state and length, and returns a new state,
    where a state is a list, starting position, and skip size."
  [[v i s] l]
  [(reverse-and-write v i l)
   (mod (+ i l s) (count v))
   (inc s)])

(defn knots [v ls]
  (reduce update-state
          [v 0 0]
          ls))

(defn advent-10-1 [v lengths]
  (apply * (take 2 (first
                    (knots v lengths)))))

(def input
  (map str->int
       (string/split
        "34,88,2,222,254,93,150,0,199,255,39,32,137,136,1,167"
        #",")))

(def v
  [0 1 2 3 4])
(def lengths
  [3 4 1 5])
