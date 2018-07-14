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

(defn parse-input [input]
  (assoc (new-state) :infected-nodes (parse-infected-nodes input)))

(defn solve [input n]
  (last (take (inc n) (iterate advance (parse-input input)))))


(comment
  (parse-input input)

  (def example-string
    "..#
  #..
  ...")

  (def example-input
    (parse-input example-string))

  (advance
   (advance example-input))
  (swap-infection
   (change-facing example-input))

  (solve example-string 10000)

  (solve input 10000)

  )

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
