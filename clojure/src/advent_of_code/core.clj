(ns advent-of-code.core
  (:require [clojure.string :as string]))

;;;; Helpers

(defn char->int [c]
  (Character/digit c 10))

(defn str->int [s]
  (Integer/parseInt s 10))

(defn tab-split [s]
  (string/split s #"\t"))

(defn ws-split [s]
  (string/split s #"\s+"))

(defn indexed [coll]
  (map-indexed vector coll))

(defn find-first [pred coll]
  (reduce (fn [_ item]
            (when (pred item) (reduced item)))
          nil
          coll))
