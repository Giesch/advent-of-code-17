(ns advent-of-code.day-9
  (:require [advent-of-code.core :refer :all]))

(def initial-state
  {:saw-bang false,
   :in-garbage false,
   :garbage-count 0,
   :brackets-deep 0,
   :score 0})

(defn in-the-bin [state ch]
  (if (= ch \>)
    (assoc state :in-garbage false)
    (update state :garbage-count inc)))

(defn counting-brackets
  [{:keys [brackets-deep score] :as state} ch]
  (case ch
    \} (merge state {:brackets-deep (dec brackets-deep)
                     :score (+ score brackets-deep)})
    \{ (update state :brackets-deep inc)
    state))

(defn read-char
  [{:keys [saw-bang in-garbage] :as state} ch]
  (cond
    saw-bang   (assoc state :saw-bang false)
    (= ch \!)  (assoc state :saw-bang true)
    in-garbage (in-the-bin state ch)
    (= ch \<)  (assoc state :in-garbage true)
    :else      (counting-brackets state ch)))

(defn parse-string [s]
  (reduce read-char
          initial-state
          s))

(defn advent-9-1 [s]
  (:score (parse-string s)))

(defn advent-9-2 [s]
  (:garbage-count (parse-string s)))
