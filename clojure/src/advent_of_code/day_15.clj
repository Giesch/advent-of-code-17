(ns advent-of-code.day-15)

(defn generator [factor previous]
  (mod (* factor previous) 2147483647))

(defn gen-A [n]
  (iterate (partial generator 16807) n))

(defn gen-B [n]
  (iterate (partial generator 48271) n))

(defn num->bin-str [n]
  (Integer/toString n 2))

(defn sixteenqual [s1 s2]
  (apply = (map #(take-last 16 %)
                [s1 s2])))

(defn gen-pairs [agen bgen]
  (map (fn [a b] (map num->bin-str [a b]))
       agen
       bgen))

(defn judge [n pairs]
  (reduce (fn [acc [a b]]
            (if (sixteenqual a b)
              (inc acc)
              acc))
          0
          (take (inc n) pairs)))

(defn this-is-a-duel-i-guess [n a b]
  (judge n (gen-pairs (gen-A a)
                      (gen-B b))))

;;;; Part 2

(defn mod-filter [n coll]
  (filter #(zero? (mod % n)) coll))

(defn how-is-this-a-duel [n a b]
  (judge n (gen-pairs (mod-filter 4 (gen-A a))
                      (mod-filter 8 (gen-B b)))))
