(ns advent-of-code.day-14
  (:require [advent-of-code.day-10 :refer [advent-10-2]]
            [advent-of-code.core :refer :all]))

;;;; Part 1

(def hex-ch->bin-vec
  {\0 [0 0 0 0],
   \1 [0 0 0 1],
   \2 [0 0 1 0],
   \3 [0 0 1 1],
   \4 [0 1 0 0],
   \5 [0 1 0 1],
   \6 [0 1 1 0],
   \7 [0 1 1 1],
   \8 [1 0 0 0],
   \9 [1 0 0 1],
   \a [1 0 1 0],
   \b [1 0 1 1],
   \c [1 1 0 0],
   \d [1 1 0 1],
   \e [1 1 1 0],
   \f [1 1 1 1]})

(defn- row-keys [s]
  (vec (map #(str s \- %) (range 128))))

(defn- row-hashes [s]
  (map advent-10-2 (row-keys s)))

(defn- row-hash->bin-vec [s]
  (mapcat hex-ch->bin-vec s))

(defn- all-squares [key-str]
  (->> key-str
       (row-hashes)
       (map row-hash->bin-vec)
       (map vec)
       (vec)))

(defn advent-14-1 [s]
  (reduce #(apply + %1 %2)
          0
          (all-squares s)))

;;;; Part 2

(defn neighbor-coordinates [row col]
  (apply clojure.set/union
         (for [r [(dec row) (inc row)]
               c [(dec col) (inc col)]
               :when (and (<= 0 r 127)
                          (<= 0 c 127))]
           #{[row c] [r col]})))

(defn lookup [squares row col]
  (if-let [r (nth squares row nil)]
    (nth r col nil)
    nil))

(defn squash-group [squares row col]
  (let [squares   (assoc-in squares [row col] 0)
        neighbors (neighbor-coordinates row col)]
    (reduce (fn [sqs [r c]] (squash-group sqs r c))
            squares
            (filter (fn [[r c]] (= 1 (lookup squares r c)))
                    neighbors))))

(defn first-one [row]
  (first (find-first #(= 1 (second %))
                     (indexed row))))

(defn one-coord [squares]
  (reduce (fn [_ [r row]]
            (if-let [c (first-one row)]
              (reduced [r c])
              nil))
          nil
          (indexed squares)))

(defn count-groups [squares]
  (loop [squares squares
         groups  0]
    (if-let [[r c] (one-coord squares)]
      (recur (squash-group squares r c)
             (inc groups))
      groups)))

(defn advent-14-2 [s]
  (count-groups (all-squares s)))
