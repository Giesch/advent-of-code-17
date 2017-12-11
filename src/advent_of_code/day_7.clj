(ns advent-of-code.day-7
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(def s  "pbga (66)
xhth (57)
ebii (61)
havc (66)
ktlj (57)
fwft (72) -> ktlj, cntj, xhth
qoyq (66)
padx (45) -> pbga, havc, qoyq
tknk (41) -> ugml, padx, fwft
jptl (61)
ugml (68) -> gyxo, ebii, jptl
gyxo (61)
cntj (57)
")

(defn read-weight
  [s]
  (str->int (string/replace s #"[()]" "")))

(defn strip-comma
  [s]
  (string/replace s #"," ""))

(defn read-children
  [[_ & names]]
  (->> names
       (map strip-comma)
       (vec)))

(defn process-line
  ([name weight]
   {name {:weight (read-weight weight)}})

  ([name weight & more]
   (let [m (process-line name weight)
         children (read-children more)]
     (assoc-in m [name :children] children))))

(defn parse-line
  [s]
  (->> s
       (ws-split)
       (apply process-line)))

(defn prepare-input
  [s]
  (->> s
       (string/split-lines)
       (map parse-line)
       (apply merge)))

(defn find-by-name
  [name m]
  (if-let [named (some (fn [[k v]] (if (= name k) [k v] nil))
                       m)
           ]
    named
    (filter #(contains? % :children)
            (vals m))
    )
  )

;; this doesn't traverse the tree
(defn first-with-children
  [m]
  (some (fn [[k v]] (if (contains? v :children)
                      [k v]
                      nil))
        m))

(defn stack-children
  "Takes the overall map and "
  [m children]

  )

(defn find-root
  [s]
  (let [m (prepare-input s)]

    )
  )
