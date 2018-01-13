(ns advent-of-code.day-18
  (:require [clojure.string :as string]
            [advent-of-code.core :refer :all]))

(def initial-state
  {:registers     {}
   :current-instr 0
   :last-sound    nil
   :recovered     nil})

(defn prepare-input [s]
  (->> s
       string/split-lines
       (map string/trim)
       (map ws-split)
       vec))

(defn register-or-value [registers s]
  (or (get registers s)
      (str->int s)))

(defn inc-instr [state]
  (update state :current-instr inc))

(defn update-registers [state f]
  (inc-instr
   (update state :registers f)))

(defn play-sound [state arg]
  (inc-instr
   (if-let [sound (get (:registers state) arg)]
     (assoc state :last-sound sound)
     state)))

(defn set-register [registers [reg arg2]]
  (assoc registers
         reg
         (register-or-value registers arg2)))

(defn add-register [registers [reg arg2]]
  (assoc registers
         reg
         (+ (get registers reg 0)
            (register-or-value registers arg2))))

(defn mod-register [registers [reg arg2]]
  (assoc registers
         reg
         (mod (get registers reg 0)
              (register-or-value registers arg2))))

(defn multiply-register [registers [reg val]]
  (assoc registers
         reg
         (* (get registers reg 0)
            (str->int val))))

(defn recover-sound [{:keys [registers] :as state} arg]
  (inc-instr
   (if-not (zero? (get registers arg 0))
     (assoc state
            :recovered
            (:last-sound state))
     state)))

(defn jump-maybe
  [{:keys [registers] :as state} [arg1 arg2]]
  (if (< 0 (register-or-value registers arg1))
    (update state
            :current-instr
            #(+ % (register-or-value registers arg2)))
    (inc-instr state)))

(defn execute-instruction
  "Returns the new state from executing current instruction."
  [instructions {:keys [current-instr] :as state}]
  (let [[instr & args] (get instructions current-instr)]
    (case instr
      "snd" (play-sound state (first args))
      "set" (update-registers state #(set-register % args))
      "add" (update-registers state #(add-register % args))
      "mul" (update-registers state #(multiply-register % args))
      "mod" (update-registers state #(mod-register % args))
      "rcv" (recover-sound state (first args))
      "jgz" (jump-maybe state args))))

(defn states [instructions]
  (iterate (partial execute-instruction instructions)
           initial-state))

(defn advent-18-1 [s]
  (->> s
       (prepare-input)
       (states)
       (find-first #(:recovered %))))

(comment

  (prepare-input input))

(def input
  "set i 31
  set a 1
  mul p 17
  jgz p p
  mul a 2
  add i -1
  jgz i -2
  add a -1
  set i 127
  set p 826
  mul p 8505
  mod p a
  mul p 129749
  add p 12345
  mod p a
  set b p
  mod b 10000
  snd b
  add i -1
  jgz i -9
  jgz a 3
  rcv b
  jgz b -1
  set f 0
  set i 126
  rcv a
  rcv b
  set p a
  mul p -1
  add p b
  jgz p 4
  snd a
  set a b
  jgz 1 3
  snd b
  set f 1
  add i -1
  jgz i -11
  snd a
  jgz f -16
  jgz a -19")
