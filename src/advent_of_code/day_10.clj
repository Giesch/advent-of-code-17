(ns advent-of-code.day-10
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

;;;; Part 1

(defn read-length [v i l]
  (->> v
       (cycle)
       (drop i)
       (take l)))

(defn write-length [v i length]
  (let [cl    (count length)
        cv    (count v)
        draft (concat (take i (cycle v))
                      length
                      (drop (+ i cl) (cycle v)))]
    (concat (take i (drop cv draft))
            (take (- cv i) (drop i draft)))))

(defn reverse-and-write [v i l]
  (->> (read-length v i l)
       (reverse)
       (write-length v i)))

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

;;;; Part 2

(defn process-input [s]
  (concat (map int s)
          [17, 31, 73, 47, 23]))

(defn hash-round
  "Takes a seq of lengths and a state, and returns a new state,
    where a state is a list, starting position, and skip size."
  [ls state]
  (reduce update-state
          state
          ls))

(defn sparse-hash [v ls]
  (->> [v 0 0]
       (iterate (partial hash-round ls))
       (take 65)
       (last)
       (first)))

(defn dense-hash [sparse]
  (map #(apply bit-xor %)
       (partition 16 sparse)))

(defn int->hex-str [i]
  (let [s (Integer/toHexString i)]
    (if (= 2 (count s))
      s
      (str "0" s))))

(defn hash->hex [dense]
  (apply str (map int->hex-str dense)))

(defn advent-10-2 [s]
  (->> s
       (process-input)
       (sparse-hash (range 256))
       (dense-hash)
       (hash->hex)))
