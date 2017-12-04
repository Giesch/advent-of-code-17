(ns advent-of-code.day-4
  (:require [clojure.string :as string]))

(defn count-valid-lines [s valid?]
  (->> s
       (string/split-lines)
       (map #(string/split % #"\s+"))
       (filter valid?)
       (count)))

;;;; Part 1

(defn repeated-words-so-far [coll]
  (map second
       (reductions
        (fn [[so-far repeated] word]
          (if (so-far word)
            [so-far (conj repeated word)]
            [(conj so-far word) repeated]))
        [#{} #{}]
        coll)))

(defn advent-4-1 [s]
  (count-valid-lines s #(not-any? seq (repeated-words-so-far %))))

;;;; Part 2

(defn repeated-anagrams-so-far [coll]
  (map second
       (reductions
        (fn [[so-far repeated] word]
          (let [anagram (sort word)]
            (if (so-far anagram)
              [so-far (conj repeated anagram)]
              [(conj so-far anagram) repeated])))
        [#{} #{}]
        coll)))

(defn advent-4-2 [s]
  (count-valid-lines s #(not-any? seq (repeated-anagrams-so-far %))))
