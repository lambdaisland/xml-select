(ns movies
  (:require
   [lambdaisland.xml-select :as xs]
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [clojure.data.xml :as xml]))

(def movies
  "<Movies>
    <Movie rating= \"R\">
        <Title runtime=\"177\" >The Godfather</Title>
        <Genre> Crime Drama </Genre>
        <Director>
            <Name>
                <First>Francis Ford </First>
                <Last>Coppola </Last>
            </Name>
        </Director>
        <Studio>Paramount Pictures </Studio>
        <Year>1972 </Year>
    </Movie>
    <Movie rating= \"R\">
        <Title runtime=\"142\">The Shwashank Redemption</Title>
        <Genre> Drama</Genre>
        <Director>
            <Name highratedmovie=\"The Mist\">
                <First> Frank   </First>
                <Last> Darabont</Last>
            </Name>
        </Director>
        <Studio>Columbia Pictures </Studio>
        <Year> 1994</Year>
    </Movie>
</Movies>")

(def doc (xml/parse-str movies))

(require 'clojure.walk)

(clojure.walk/postwalk (fn [m]
                         (if (:content m)
                           (update m :content (comp vec (partial remove #(and (string? %) (clojure.string/blank? %)))))
                           m))
                       doc)

(def doc
  {:tag :Movies
   :content
   [{:tag :Movie
     :attrs {:rating "R"}
     :content
     [{:tag :Title :attrs {:runtime "177"} :content ["The Godfather"]}
      {:tag :Genre :content ["Crime Drama"]}
      {:tag :Director
       :content
       [{:tag :Name
         :content
         [{:tag :First :content ["Francis Ford"]}
          {:tag :Last :content ["Coppola"]}]}]}
      {:tag :Studio :content ["Paramount Pictures"]}
      {:tag :Year :content ["1972"]}]}
    {:tag :Movie
     :attrs {:rating "R"}
     :content
     [{:tag :Title
       :attrs {:runtime "142"}
       :content ["The Shwashank Redemption"]}
      {:tag :Genre :content ["Drama"]}
      {:tag :Director
       :content
       [{:tag :Name
         :attrs {:highratedmovie "The Mist"}
         :content
         [{:tag :First :content ["Frank"]}
          {:tag :Last :content ["Darabont"]}]}]}
      {:tag :Studio :content ["Columbia Pictures"]}
      {:tag :Year :content ["1994"]}]}]})

;; Select by tag
(xs/select [:Year] doc)
;; => ({:tag :Year, :content ["1972"]} {:tag :Year, :content ["1994"]})

;; Descendants
(xs/select [:Movies :Title] doc)
;; => ({:tag :Title, :attrs {:runtime "177"}, :content ["The Godfather"]}
;;     {:tag :Title, :attrs {:runtime "142"}, :content ["The Shwashank Redemption"]})


;; Direct Descendant
(xs/select [:Movie :> :Title] doc)

;; Arbitary function
(xs/select [:Movies :> #(= {:rating "R"} (:attrs %))] doc)

;; Attribute
(xs/select [{:runtime "142"}] doc)
;; => ({:tag :Title, :attrs {:runtime "142"}, :content ["The Shwashank Redemption"]})

;; Can take a regex
(xs/select [{:runtime #"\d{3}"}] doc)
;; => ({:tag :Title, :attrs {:runtime "177"}, :content ["The Godfather"]}
;;     {:tag :Title, :attrs {:runtime "142"}, :content ["The Shwashank Redemption"]})

;; Can take a function
(xs/select [{:runtime #(< 150 (parse-long %) 180)}] doc)
;; => ({:tag :Title, :attrs {:runtime "177"}, :content ["The Godfather"]})

;; Vector to combine multiple predicates
(xs/select [:Movie [:Title {:runtime "177"}]] doc)
;; => ({:tag :Title, :attrs {:runtime "177"}, :content ["The Godfather"]})

;; String matches a text node, or the combined string value of all descendant text nodes
(xs/select ["Coppola"] doc)
;; => ({:tag :Last, :content ["Coppola"]} "Coppola")


;; Compose to have "having" semantics, e.g. the year of all movies with genre "Crime drama"
(xs/select [:Movies :> #(xs/select [:Genre "Crime Drama"] %) :Year] doc)
;; => ({:tag :Movie,
;;      :attrs {:rating "R"},
;;      :content
;;      [{:tag :Title, :attrs {:runtime "177"}, :content ["The Godfather"]}
;;       {:tag :Genre, :content ["Crime Drama"]}
;;       {:tag :Director,
;;        :content
;;        [{:tag :Name,
;;          :content
;;          [{:tag :First, :content ["Francis Ford"]}
;;           {:tag :Last, :content ["Coppola"]}]}]}
;;       {:tag :Studio, :content ["Paramount Pictures"]}
;;       {:tag :Year, :content ["1972"]}]})
