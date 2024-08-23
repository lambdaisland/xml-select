(ns lambdaisland.xml-select-test
  (:require
   [lambdaisland.xml-select :as xs]
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [clojure.data.xml :as xml]))

(def doc (xml/parse-str (slurp (io/resource "test_file.xml"))))

(deftest basic-selectors
  (are [sel res] (= (xs/select sel doc) res)
    ;; Empty selector selects nothing
    [] nil

    ;; Keyword selects :tag
    [:rights]
    [{:tag :rights
      :attrs {}
      :content ["Public Domain (PianoXML typeset)"]}]

    ;; multiple selectors finds any descendant
    [:identification :software]
    [{:tag :software
      :attrs {}
      :content ["MuseScore 2.0.3"]}]

    ;; :> for direct descendant
    [:identification :> :software]
    nil

    [:encoding :> :software]
    [{:tag :software
      :attrs {}
      :content ["MuseScore 2.0.3"]}]

    ;; Map for attributes, accepts regex, function, value
    [:part-list {:print-object "no"}]
    [{:tag :part-name, :attrs {:print-object "no"}, :content ["Piano"]}]

    [{:element #"(accidental|beam)"}]
    [{:tag :supports, :attrs {:element "accidental", :type "yes"}, :content ()}
     {:tag :supports, :attrs {:element "beam", :type "yes"}, :content ()}]

    [{:default-y #(< 0 (parse-double %) 6)} :octave]
    '[{:tag :octave, :attrs {}, :content ("5")}
      {:tag :octave, :attrs {}, :content ("5")}
      {:tag :octave, :attrs {}, :content ("5")}
      {:tag :octave, :attrs {}, :content ("5")}
      {:tag :octave, :attrs {}, :content ("5")}
      {:tag :octave, :attrs {}, :content ("5")}
      {:tag :octave, :attrs {}, :content ("5")}]

    ;; Vector to check multiple things on the same element
    [:defaults [:word-font {:font-size "10"}]]
    [{:tag :word-font
      :attrs {:font-family "FreeSerif", :font-size "10"}
      :content ()}]))
