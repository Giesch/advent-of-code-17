(ns advent-of-code.day-22
  (:require [clojure.string :as string]))

(defn new-state []
  {:current-node      [0 0]
   :facing            :up
   :infected-nodes    (hash-set)
   :infections-caused 0
   :bursts-executed   0})

(def directions
  {:up    [0 1]
   :down  [0 -1]
   :left  [-1 0]
   :right [1 0]})

(defn add-points [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn turn-left [dir]
  (case dir
    :up    :left
    :down  :right
    :left  :down
    :right :up))

(defn turn-right [dir]
  (case dir
    :up    :right
    :down  :left
    :left  :up
    :right :down))

(defn reverse-direction [dir]
  (case dir
    :up    :down
    :down  :up
    :left  :right
    :right :left))

(defn change-facing
  [{:keys [infected-nodes facing current-node] :as state}]
  (assoc state :facing (if (infected-nodes current-node)
                         (turn-right facing)
                         (turn-left facing))))

(defn swap-infection [state]
  (let [infected-nodes (:infected-nodes state)
        coord          (:current-node state)
        infected?      (infected-nodes coord)
        update-fn      (if infected? disj conj)
        inc-infections (not infected?)]
    (-> (if inc-infections
          (update state :infections-caused inc)
          state)
        (update :infected-nodes #(update-fn % coord)))))

(defn move-forward [state]
  (let [dir ((:facing state) directions)]
    (update state :current-node #(add-points dir %))))

(defn advance [state]
  (-> state
      change-facing
      swap-infection
      move-forward
      (update :bursts-executed inc)))

(defn parse-infected-nodes [input]
  (let [lines       (string/split-lines input)
        line->bools (fn [line] (vec (map #(= % \#) line)))
        side-len    (count lines) ;; assuming it's a square
        shift       (quot side-len 2)
        two-d-ify   (fn [n infected] [[(- (mod n side-len) shift)
                                       (- (- (quot n side-len) shift))]
                                      infected])]
    (into (hash-set)
          (comp (map string/trim)
                (mapcat line->bools)
                (map-indexed two-d-ify)
                (filter (fn [[position infected]] infected))
                (map (fn [[position infected]] position)))
          lines)))

(defn parse-input [input nodes-parser]
  (assoc (new-state) :infected-nodes (nodes-parser input)))

(defn solve [input n]
  (last (take (inc n) (iterate advance (parse-input input parse-infected-nodes)))))

(defn part-2-state []
  {:current-node      [0 0]
   :facing            :up
   :node-states       (hash-map) ;; :clean :infected :weakened :flagged
   :infections-caused 0
   :bursts-executed   0})

(defn parse-node-states [input]
  (let [lines       (string/split-lines input)
        line->bools (fn [line] (vec (map #(= % \#) line)))
        side-len    (count lines) ;; assuming it's a square
        shift       (quot side-len 2)
        two-d-ify   (fn [n infected] [[(- (mod n side-len) shift)
                                       (- (- (quot n side-len) shift))]
                                      infected])]
    (into (hash-map)
          (comp (map string/trim)
                (mapcat line->bools)
                (map (fn [b] (if b :infected :clean)))
                (map-indexed two-d-ify))
          lines)))

(defn parse-part-2 [input]
  (assoc (part-2-state) :node-states (parse-node-states input)))

(defn part-2-change-facing [{:keys [current-node node-states facing] :as state}]
  (assoc state :facing (case (get node-states current-node :clean)
                         :clean    (turn-left facing)
                         :infected (turn-right facing)
                         :weakened facing
                         :flagged (reverse-direction facing))))

(defn part-2-modify-node [{:keys [current-node] :as state}]
  (let [increment-infections (fn [state] (if (= :infected (get-in state [:node-states current-node]))
                                           (update state :infections-caused inc)
                                           state))]
    (-> state
        (update-in [:node-states current-node]
                   #(case %
                      :clean :weakened
                      :infected :flagged
                      :weakened :infected
                      :flagged :clean
                      :weakened))
        increment-infections)))

(defn part-2-advance [state]
  (-> state
      part-2-change-facing
      part-2-modify-node
      move-forward
      (update :bursts-executed inc)))

(defn solve-part-2 [input n]
  (last (take (inc n) (iterate part-2-advance (parse-part-2 input)))))

(comment
  (parse-input input parse-infected-nodes)
  (parse-part-2 input)

  (def example-string
    "..#
  #..
  ...")

  (def example-input
    (parse-input example-string parse-infected-nodes))

  (advance
   (advance example-input))
  (swap-infection
   (change-facing example-input))

  (part-2-advance (parse-part-2 example-string))

  (solve-part-2 example-string 100)
  (solve-part-2 example-string 10000000)
  (solve-part-2 input 10000000)

  (solve example-string 10000)

  (solve input 10000))

(def input
  "#.###...#..#..#...##.####
  ##.##.#..##.#..#.#..#####
  .####..###.#.#####.#.##.#
  ##..#.##.#.#.#...#..##..#
  ..#...####.#.###.###...#.
  #..###.##.###.....#....#.
  .#..#.##.##....##...####.
  ###.##....#...#.##....##.
  ..#.###..######.#.####...
  .#.###..#.##.#..##.######
  ###.####.#####.####....#.
  #...####.#.##...##..#.#..
  ##.######.#....##.#.####.
  .#.#..#...##....#....#...
  .####.##.#..##...#..####.
  .#.####.##..###..###..##.
  ...#...####...#.#.#.###.#
  #.##.####.#..##.###.####.
  .#.#...####....##..####.#
  ##.###.##..####..#.######
  #.#...#.#.##.####........
  .......#..##..#.#..###...
  .#..###.###........##.#..
  .######.......#.#.##.#.#.
  .##..#.###.....##.#.#...#")
