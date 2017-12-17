(ns advent-of-code.day-8
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(def condition-fns
  {">" >,
   ">=" >=,
   "<" <,
   "<=" <=,
   "==" =,
   "!=" not=})

(defn parse-line
  [[reg instr arg _ & condition]]
  {:register reg,
   :instruction instr,
   :argument (str->int arg),
   :condition condition})

(defn prepare-input [s]
  (->> s
       (string/split-lines)
       (map ws-split)
       (remove empty?)
       (map parse-line)))

;;;; Part 1

(defn check-cond
  [registers, [reg fn-name const]]
  ((get condition-fns fn-name)
   (get registers reg 0)
   (str->int const)))

(defn update-registers
  [rs r f arg]
  (update rs
          r
          (fn [old f arg]
            (if (nil? old)
              (f arg)
              (f old arg)))
          f
          arg))

(defn execute [rs r instr arg]
  (case instr
    "inc" (update-registers rs r + arg)
    "dec" (update-registers rs r - arg)))

(defn interpret [registers parsed-line]
  (if (check-cond registers
                  (:condition parsed-line))
    (execute registers
             (:register parsed-line)
             (:instruction parsed-line)
             (:argument parsed-line))
    registers))

(defn run [parsed-lines]
  (reduce interpret
          {}
          parsed-lines))

(defn max-register-value [rs]
  (apply max (vals rs)))

(defn advent-8-1 [s]
  (->> s
       (prepare-input)
       (run)
       (max-register-value)))

;;;; Part 2

(defn interpret-with-max
  [[mx registers] parsed-line]
  (if (check-cond registers
                  (:condition parsed-line))
    (let [rs (execute registers
                      (:register parsed-line)
                      (:instruction parsed-line)
                      (:argument parsed-line))
          mx (max mx (max-register-value rs))] ;; wasteful
      [mx rs])
    [mx registers]))

(defn run-with-max [parsed-lines]
  (reduce interpret-with-max
          [Integer/MIN_VALUE {}]
          parsed-lines))

(defn advent-8-2 [s]
  (->> s
       (prepare-input)
       (run-with-max)
       (first)))
