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
       (string/split-lines)
       (map string/trim)
       (map ws-split)
       (vec)))

(defn register-or-value [registers s]
  (or (get registers s)
      (str->int s)))

(defn inc-instr [state]
  (update state :current-instr inc))

(defn update-registers [state f]
  (inc-instr
   (update state :registers f)))

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

(defn play-sound [state arg]
  (inc-instr
   (if-let [sound (get (:registers state) arg)]
     (assoc state :last-sound sound)
     state)))

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
       (find-first :recovered)))

;;;; Part 2

(defn init-process [p]
  {:registers     {"p" p}
   :current-instr 0
   :total-sent    0})

;; global mutable state is a good idea

(def inbox-0 (atom []))

(def inbox-1 (atom []))

(defn send-fn [inbox]
  (fn [msg]
    (swap! inbox conj msg)))

;; thanks SO
(defn recieve-fn [inbox]
  (fn []
    (if-let [msg (first @inbox)]
      (do (swap! inbox #(subvec % 1)) ;; yay side effects?
          msg)
      nil)))

(def world-state
  (let [process-0 (init-process 0)
        process-1 (init-process 1)]
    {:process-0 (merge process-0 {:send!    (send-fn inbox-1)
                                  :recieve! (recieve-fn inbox-0)})
     :process-1 (merge process-1 {:send!    (send-fn inbox-0)
                                  :recieve! (recieve-fn inbox-1)})}))

(defn send-message
  [{:keys [registers send!] :as state} reg]
  (do (send! (get registers reg 0))
      (inc-instr (update state :total-sent inc))))

(defn recieve-message
  [{:keys [recieve!] :as state} reg]
  (if-let [msg (recieve!)]
    (inc-instr (update state
                       :registers
                       #(assoc % reg msg)))
    state))

(defn execute-instruction-2
  "Returns the new state from executing current instruction."
  [instructions {:keys [current-instr] :as state}]
  (let [[instr & args] (get instructions current-instr)]
    (case instr
      "snd" (send-message state (first args))
      "set" (update-registers state #(set-register % args))
      "add" (update-registers state #(add-register % args))
      "mul" (update-registers state #(multiply-register % args))
      "mod" (update-registers state #(mod-register % args))
      "rcv" (recieve-message state (first args))
      "jgz" (jump-maybe state args))))

;; thanks 2011 blog
(defn update-values [m f]
  (into {} (for [[k v] m]
             [k (f v)])))

(defn states-2 [instructions]
  (iterate
   #(update-values % (partial execute-instruction-2 instructions))
   world-state))

(defn advent-18-2 [s]
  (->> s
       (prepare-input)
       (states-2)
       (partition 2 1)
       (take-while (fn [[a b]] (not= a b)))
       (last)
       (first)
       (#(get-in % [:process-1 :total-sent]))))
