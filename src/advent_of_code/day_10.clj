(ns advent-of-code.day-10
  (:require [advent-of-code.core :refer :all]
            [clojure.string :as string]))

(defn advent-10-1 [v lengths])

(def input
  (map str->int
       (string/split
        "34,88,2,222,254,93,150,0,199,255,39,32,137,136,1,167"
        #",")))
