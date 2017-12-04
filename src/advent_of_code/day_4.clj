(ns advent-of-code.day-4
  (:require [clojure.string :as string]))

(defn count-valid-lines [s valid?]
  (->> s
       (string/split-lines)
       (map #(string/split % #"\s+"))
       (filter valid?)
       (count)))

;;;; Part 1

(defn repeated-words [words]
  (second
   (reduce
    (fn [[so-far repeated] word]
      (if (so-far word)
        [so-far (conj repeated word)]
        [(conj so-far word) repeated]))
    [#{} #{}]
    words)))

(defn advent-4-1 [s]
  (count-valid-lines s #(empty? (repeated-words %))))

;;;; Part 2

(defn repeated-anagrams [words]
  (second
   (reduce
    (fn [[so-far repeated] word]
      (let [anagram (sort word)]
        (if (so-far anagram)
          [so-far (conj repeated anagram)]
          [(conj so-far anagram) repeated])))
    [#{} #{}]
    words)))

(defn advent-4-2 [s]
  (count-valid-lines s #(empty? (repeated-anagrams %))))
