(ns advent-of-code.day-9
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

;; ;; initial state
;; {:saw-bang false,
;;  :in-garbage false,
;;  :brackets-deep 0
;;  :score 0}

(defn look-for-end-garbage
  "When saw-bang is false, in-garbage is true, and ch != !"
  [state ch]
  (if (= ch \>)
    (assoc state :in-garbage false)
    (update state :garbage-count inc)))

(defn close-bracket
  [{:keys [brackets-deep score] :as state}]
  (conj state
        {:brackets-deep (dec brackets-deep)
         :score (+ score brackets-deep)}))

(defn open-bracket
  [state]
  (update state :brackets-deep inc))

(defn count-brackets
  "When we're in clean brackets land."
  [state ch]
  (cond
    (= ch \}) (close-bracket state)
    (= ch \{) (open-bracket state)
    :else state))

(defn read-char
  [{:keys [saw-bang in-garbage brackets-deep score] :as state} ch]
  (cond
    saw-bang   (assoc state :saw-bang false)
    (= ch \!)  (assoc state :saw-bang true)
    in-garbage (look-for-end-garbage state ch)
    (= ch \<)  (assoc state :in-garbage true)
    :else      (count-brackets state ch)))

(defn parse-string [s]
  (reduce read-char
          {:saw-bang false,
           :in-garbage false,
           :garbage-count 0,
           :brackets-deep 0,
           :score 0}
          s))

(defn advent-9-1 [s]
  (:score (parse-string s)))

(defn advent-9-2 [s]
  (:garbage-count (parse-string s)))
