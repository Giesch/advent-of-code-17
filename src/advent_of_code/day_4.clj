(ns advent-of-code.day-4
  (:require [clojure.string :as string]))

(defn count-valid-lines [s valid?]
  (->> s
       (string/split-lines)
       (map #(string/split % #"\s+"))
       (filter valid?)
       (count)))

(defn no-repeated [words store-as]
  (loop [s #{}
         ws words]
    (if (empty? ws)
      true
      (let [w (store-as (first ws))]
        (if (s w)
          false
          (recur (conj s w)
                 (rest ws)))))))

;;;; Part 1

(defn no-repeated-words [words]
  (no-repeated words identity))

(defn advent-4-1 [s]
  (count-valid-lines s no-repeated-words))

;;;; Part 2

(defn no-repeated-anagrams [words]
  (no-repeated words sort))

(defn advent-4-2 [s]
  (count-valid-lines s no-repeated-anagrams))
