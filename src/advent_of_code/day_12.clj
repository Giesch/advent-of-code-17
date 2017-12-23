(ns advent-of-code.day-12
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn parse-line [[num _ & neighbors]]
  [(str->int num)
   (vec (map read-string neighbors))])

(defn prepare-input [s]
  (->> s
       (string/split-lines)
       (map ws-split)
       (map parse-line)
       (into {})))

(defn dijkstra [m start]
  (loop [connected #{start}
         neighbors (get m start)]
    (if (empty? neighbors)
      connected
      (recur (conj connected (first neighbors))
             (remove connected
                     (concat (rest neighbors)
                             (get m (first neighbors))))))))

(defn advent-12-1 [s]
  (-> s
      (prepare-input)
      (dijkstra 0)
      (count)))

(defn clean-map [m group]
  (into {} (map (fn [[k v]]
                  [k (remove group v)])
                (remove (fn [[k _]] (group k))
                        m))))

(defn count-groups [m]
  (loop [m m
         c 0]
    (if (empty? m)
      c
      (let [group (dijkstra m (first (keys m)))]
        (recur (clean-map m group)
               (inc c))))))

(defn advent-12-2 [s]
  (count-groups (prepare-input s)))
