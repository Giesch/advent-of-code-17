(ns advent-of-code.core
  (:require [clojure.string :as string]))

;;;; Helpers

(defn char->digit [c]
  (Character/digit c 10))

(defn str->int [s]
  (Integer/parseInt s 10))

(defn tab-split [s]
  (string/split s #"\t"))
