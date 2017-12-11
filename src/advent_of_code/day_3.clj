(ns advent-of-code.day-3)

;;;; Part 1

(defn side-distances [lower]
  (let [upper (* 2 lower)]
    (concat (range (dec upper) lower -1)
            (range lower (inc upper) 1))))

(defn square-distances [n]
  (apply concat (repeat 4 (side-distances n))))

(def distances
  (apply concat
         '(0)
         (map square-distances
              (iterate inc 1))))

(defn advent-3-1
  "Finds the manhattan distance to the center of a spiral-indexed square."
  [n]
  (last (take n distances)))

;;;; Part 2

(def odd-squares
  (map #(* % %)
       (filter odd? (range))))

(defn indexed [coll]
  (map-indexed vector coll))

(defn last-index-for [pred coll]
  (first (last (take-while #(pred (second %))
                           (indexed coll)))))

(defn which-level [n]
  (#(if (nil? %) 0 (inc %))
   (last-index-for #(< % n)
                   odd-squares)))

(defn side-length [idx]
  (* 2 (which-level idx)))

(defn set-of-corners [x]
  (let [l (side-length x)]
    [(- x (* 3 l)) (- x (* 2 l)) (- x l) x]))

(def corners
  (mapcat set-of-corners
          odd-squares))

(defn corner? [idx]
  (= idx (last
          (take-while #(<= % idx)
                      corners))))

(defn corner-idx [idx]
  (last-index-for #(<= % idx)
                  corners))

(defn inner-corner [idx]
  (nth corners (- (corner-idx idx) 4)))


(defn sum-at-idx [idx]
  (cond
    (= idx 0) 1
    (corner? idx) (+ (sum-at-idx (- idx 1))
                     (sum-at-idx (inner-corner idx)))
    :else 0))
