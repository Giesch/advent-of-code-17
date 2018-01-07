(ns advent-of-code.day-16
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(def initial
  (vec "abcdefghijklmnop"))

(defn arg [s]
  (apply str (rest s)))

(defn arg-pair [s]
  (string/split (arg s) #"/"))

(defn parse-instruction [s]
  (case (first s)
    \s [:spin     (str->int (arg s))]
    \x [:exchange (vec (map str->int (arg-pair s)))]
    \p [:partner  [(second s) (last s)]]))

(defn prepare-input [s]
  (map parse-instruction
       (string/split s #",")))

(defn spin [programs n]
  (let [c (count programs)
        offset (- c n)]
    (into (subvec programs offset)
          (subvec programs 0 offset))))

(defn exchange [programs [a b]]
  (let [program-a (get programs a)
        program-b (get programs b)]
    (vec (assoc programs
                a program-b
                b program-a))))

(defn partner [programs [a b]]
  (vec (map #(cond (= a %) b
                   (= b %) a
                   :else %)
            programs)))

(defn dance-move
  [programs [instruction arg]]
  (case instruction
    :spin     (spin programs arg)
    :exchange (exchange programs arg)
    :partner  (partner programs arg)))

(defn dance [instructions programs]
  (reduce dance-move
          programs
          instructions))

(defn advent-16-1 [s]
  (apply str
         (dance (prepare-input s)
                initial)))

;;;; Part 2

(defn find-cycle [coll]
  (let [fst (first coll)]
    (conj (take-while #(not= fst %) (rest coll))
          fst)))

(defn like-a-billion
  [instructions programs]
  (let [repeating-cycle (find-cycle (iterate (partial dance instructions)
                                             programs))]
    (nth repeating-cycle
         (mod 1000000000 (count repeating-cycle)))))

(defn advent-16-2 [s]
  (like-a-billion (prepare-input s) initial))
