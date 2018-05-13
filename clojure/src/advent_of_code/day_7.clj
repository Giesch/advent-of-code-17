(ns advent-of-code.day-7
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn read-weight [s]
  (str->int (string/replace s #"[()]" "")))

(defn strip-comma [s]
  (string/replace s #"," ""))

(defn process-line
  ([name weight]
   {name {:weight (read-weight weight)}})
  ([name weight _ & child-names]
   (let [m (process-line name weight)
         children (vec (map strip-comma
                            child-names))]
     (assoc-in m [name :children] children))))

(defn parse-line [tokens]
  (apply process-line tokens))

(defn prepare-input [s]
  (->> s
       (string/split-lines)
       (map ws-split)
       (map parse-line)
       (apply merge)))

;;;; Part 1

(defn children-set [m]
  (into #{} (mapcat :children
                    (vals m))))

(defn find-root [m]
  (let [children (children-set m)]
    (filter #(not (children %))
            (keys m))))

(defn advent-7-1 [s]
  (find-root
   (prepare-input s)))

;;;; Part 2

(defn get-weight [m name]
  (:weight (get m name) 0))

(defn get-children [m name]
  (:children (get m name) []))

;; probably should use reduce somehow
;; but it doesn't blow stack i guess
(defn sum-weight [m name]
  (apply +
         (get-weight m name)
         (map #(sum-weight m %)
              (get-children m name))))

(defn child-weights [m name]
  (into {}
        (map (fn [name]
               [name (sum-weight m name)])
             (get-children m name))))

(defn balanced? [m name]
  (let [children (get-children m name)
        sum-weight (partial sum-weight m)]
    (if (empty? children)
      true
      (apply = (map sum-weight children)))))

(defn group-by-balance [m]
  (group-by (partial balanced? m)
            (keys m)))

(defn unbalanced [m]
  (get (group-by-balance m) false))

(defn smallest-unbalanced [m]
  (apply min-key
         (partial sum-weight m)
         (unbalanced m)))

(defn children-by-weight [m name]
  (reduce (fn [weights-map child]
            (update weights-map
                    (sum-weight m child)
                    #(conj % child)))
          {}
          (get-children m name)))

(defn problem-child [weights-map]
  (first (val
          (find-first #(= 1 (count (val %)))
                      weights-map))))

(defn find-siblings [m name]
  (filter #(not (#{name} %))
          (:children
           (find-first #(some #{name} (:children %))
                       (vals m)))))

(defn diff-with-sibling-weight [m name]
  (- (first
      (map (partial sum-weight m) (find-siblings m name)))
     (sum-weight m name)))

(defn find-correct-weight [m weights-map]
  (let [problem-child (problem-child weights-map)
        diff (diff-with-sibling-weight m problem-child)]
    (+ (get-weight m problem-child)
       diff)))

(defn new-weight [m]
  (->> m
       (smallest-unbalanced)
       (children-by-weight m)
       (find-correct-weight m)))

(defn advent-7-2 [s]
  (new-weight (prepare-input s)))
