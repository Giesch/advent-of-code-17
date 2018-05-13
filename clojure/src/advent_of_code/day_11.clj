(ns advent-of-code.day-11
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn distance-from-center
  [{:keys [northwest southwest east]}]
  (apply + (filter #(>= % 0) [northwest southwest east])))

(defn step
  [{:keys [northwest southwest east furthest] :as coord} s]
  (merge coord
         (case s
           "n"  {:northwest (inc northwest)
                 :southwest (dec southwest)}
           "s"  {:northwest (dec northwest)
                 :southwest (inc southwest)}
           "ne" {:southwest (dec southwest)
                 :east      (inc east)}
           "sw" {:southwest (inc southwest)
                 :east      (dec east)}
           "se" {:northwest (dec northwest)
                 :east      (inc east)}
           "nw" {:northwest (inc northwest)
                 :east      (dec east)})
         {:furthest (max furthest (distance-from-center coord))}))

(defn find-coord [s]
  (reduce step
          {:northwest 0, :southwest 0, :east 0, :furthest 0}
          (string/split s #",")))

(defn advent-11-1 [s]
  (distance-from-center (find-coord s)))

(defn advent-11-2 [s]
  (:furthest (find-coord s)))
