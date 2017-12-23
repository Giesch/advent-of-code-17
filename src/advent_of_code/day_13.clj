(ns advent-of-code.day-13
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn- parse-line [s]
  (let [[k r] (map str->int (string/split s #": "))]
    [k {:range r, :position 1, :direction :up}]))

(defn- prepare-input [s]
  (->> s
       (string/split-lines)
       (map parse-line)
       (into {})))

(defn- guard-walk
  [{:keys [range position direction] :as layer}]
  (case direction
    :up   (if (< position range)
            (update layer :position inc)
            (merge layer {:position (dec position),
                          :direction :down}))
    :down (if (> position 1)
            (update layer :position dec)
            (merge layer {:position (inc position),
                          :direction :up}))))

(defn- move-sentries [layers]
  (into {} (map (fn [[k v]] [k (guard-walk v)])
                layers)))

(defn- calculate-severity
  [severity packet layers]
  (+ severity (* packet
                 (get-in layers [packet :range]))))

(defn- collision-detection
  [{:keys [layers packet severity] :as state}]
  (if (= 1 (get-in layers [packet :position]))
    (merge state
           {:severity (calculate-severity severity packet layers),
            :caught true})
    state))

(defn- move-player
  [{:keys [layers packet severity] :as state}]
  (->> (update state :packet inc)
       (collision-detection)
       (merge state)))

(defn- tick [state]
  (-> state
      (update :layers move-sentries)
      (move-player)))

(defn- initial-state [s]
  (merge {:packet 0, :severity 0, :caught false}
         {:layers (prepare-input s)}))

(defn advent-13-1 [s]
  (let [state (initial-state s)
        end   (apply max (keys (:layers state)))]
    (:severity
     (nth (iterate tick state) end))))

;;;; Part 2

;; this solution is pretty dumb/slow
;; there's gotta be a faster way to do it

(defn- dead? [state end]
  (loop [{:keys [caught packet] :as s} (collision-detection state)]
    (cond
      caught         true
      (> packet end) false
      :else          (recur (tick s)))))

(defn advent-13-2 [s]
  (let [initial (initial-state s)
        end     (apply max (keys (:layers initial)))]
    (loop [i     0
           state initial]
      (if-not (dead? state end)
        i
        (recur (inc i)
               (update state :layers move-sentries))))))
