(ns advent-of-code.day-14
  (:require [advent-of-code.day-10 :refer [advent-10-2]]))

(def hex-ch->bin-vec
  {\0 [0 0 0 0],
   \1 [0 0 0 1],
   \2 [0 0 1 0],
   \3 [0 0 1 1],
   \4 [0 1 0 0],
   \5 [0 1 0 1],
   \6 [0 1 1 0],
   \7 [0 1 1 1],
   \8 [1 0 0 0],
   \9 [1 0 0 1],
   \a [1 0 1 0],
   \b [1 0 1 1],
   \c [1 1 0 0],
   \d [1 1 0 1],
   \e [1 1 1 0],
   \f [1 1 1 1]})

(defn- row-keys [s]
  (vec (map #(str s \- %) (range 128))))

(defn- row-hashes [s]
  (map advent-10-2 (row-keys s)))

(defn- row-hash->bin-vec [s]
  (apply concat (map hex-ch->bin-vec s)))

(defn- all-squares [key-str]
  (map row-hash->bin-vec
       (row-hashes key-str)))

(defn advent-14-1 [s]
  (reduce #(apply + %1 %2)
          0
          (all-squares s)))

(comment

  (def squares (all-squares "jxqlasbh"))

  (advent-14-1 "jxqlasbh")

  )
