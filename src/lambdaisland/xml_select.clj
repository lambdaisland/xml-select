(ns lambdaisland.xml-select)

(defn text [el]
  (if (string? el)
    el
    (apply str (mapcat text (:content el)))))

(defn match1 [s el]
  (cond
    (string? s)
    (= s (text el))

    (instance? java.util.regex.Pattern s)
    (re-find s (text el))

    (keyword? s)
    (= (:tag el) s)

    (map? s)
    (every? (fn [[k v]]
              (and (contains? (:attrs el) k)
                   (cond
                     (instance? java.util.regex.Pattern v)
                     (re-find v (get-in el [:attrs k]))

                     (fn? v)
                     (v (get-in el [:attrs k]))

                     :else
                     (= v (get-in el [:attrs k])))))
            s)

    (vector? s)
    (every? #(match1 % el) s)

    (fn? s)
    (s el)

    :else
    false))

(declare select)

(defn select1 [selector el]
  (when (match1 (first selector) el)
    (if (= 1 (count selector))
      [el]
      (mapcat #(select (rest selector) %) (:content el)))))

(defn select
  "Select nodes in a clojure.data.xml style data structure, based on a selector."
  [selector root]
  (seq
   (cond
     (empty? selector)
     nil
     (= :> (first selector))
     (select1 (rest selector) root)
     :else
     (mapcat (partial select1 selector) (xml-seq root)))))
