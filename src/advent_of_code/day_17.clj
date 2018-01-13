(ns advent-of-code.day-17)

(def initial
  {:buffer   [0]
   :position 0})

(defn insert
  [v position value]
  (let [position' (inc position)]
    (apply conj
           (subvec v 0 position')
           value
           (subvec v position'))))

(defn update-state
  [step {:keys [buffer position]} value]
  (let [position' (mod (+ step position) (count buffer))]
    {:buffer   (insert buffer position' value)
     :position (inc position')}))

(defn run [step n]
  (reduce (partial update-state step)
          initial
          (range 1 n)))

(defn advent-17-1 [step]
  (let [final (run step 2018)]
    (inc (:position final))))

;;;; Part 2

(def part-2-initial
  {:after-zero 1
   :length     2
   :position   1})

(defn update-state-2
  [step {:keys [after-zero length position] :as state} value]
  (let [position (mod (+ step position) length)]
    (merge (if (zero? position)
             (assoc state :after-zero value)
             state)
           {:length   (inc length),
            :position (inc position)})))

(defn run-2 [step n]
  (reduce (partial update-state-2 step)
          part-2-initial
          (range 2 n)))

(defn advent-17-2 [step]
  (:after-zero (run-2 step 50000001)))
