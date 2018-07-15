(ns advent-of-code.day-23
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn string->int-or-char [s]
  (try
    (str->int s)
    (catch NumberFormatException e
      (first s))))

(defn parse-args [[instr & args]]
  (into [instr] (map string->int-or-char) args))

(defn parse-input [input]
  (let [lines (string/split-lines input)]
    (into []
          (comp (map string/trim)
                (map #(string/split % #" "))
                (map (fn [[instr a1 a2]] [(keyword instr) a1 a2]))
                (map parse-args))
          lines)))

(defn new-state []
  {:done            false
   :registers       {}
   :program-counter 0
   :muls-invoked    0})

(defn get-value [registers arg]
  (if (char? arg)
    (get registers arg 0)
    arg))

(defn set-register [{registers :registers :as state} ch arg]
  (-> state
      (assoc-in [:registers ch] (get-value registers arg))
      (update :program-counter inc)))

(defn sub-register [{registers :registers :as state} ch arg]
  (-> state
      (update-in [:registers ch] #(- (or % 0) (get-value registers arg)))
      (update :program-counter inc)))

(defn mul-register [{registers :registers :as state} ch arg]
  (-> state
      (update-in [:registers ch] #(* % (get-value registers arg)))
      (update :muls-invoked inc)
      (update :program-counter inc)))

(defn jump-not-zero [{:keys [registers program-counter] :as state} arg1 arg2]
  (assoc state :program-counter (if (= 0 (get-value registers arg1))
                                  (inc program-counter)
                                  (+ program-counter arg2))))

(def instruction-fns
  {:set set-register
   :mul mul-register
   :sub sub-register
   :jnz jump-not-zero})

(defn execute-instruction [state [instr arg1 arg2]]
  ((instr instruction-fns) state arg1 arg2))

(defn step [instructions {:keys [registers program-counter] :as state}]
  (if-let [instr (get instructions program-counter)]
    (execute-instruction state instr)
    (assoc state :done true)))

(defn run-program []
  (last (take-while #(not (:done %))
                    (iterate (partial step (parse-input input-string)) (new-state)))))


(comment
  (parse-input input-string)

  (run-program)

  ;;
  )

(def input-string
  "set b 93
  set c b
  jnz a 2
  jnz 1 5
  mul b 100
  sub b -100000
  set c b
  sub c -17000
  set f 1
  set d 2
  set e 2
  set g d
  mul g e
  sub g b
  jnz g 2
  set f 0
  sub e -1
  set g e
  sub g b
  jnz g -8
  sub d -1
  set g d
  sub g b
  jnz g -13
  jnz f 2
  sub h -1
  set g b
  sub g c
  jnz g 2
  jnz 1 3
  sub b -17
  jnz 1 -23")
