# lambdaisland/xml-select

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/com.lambdaisland/xml-select)](https://cljdoc.org/d/com.lambdaisland/xml-select) [![Clojars Project](https://img.shields.io/clojars/v/com.lambdaisland/xml-select.svg)](https://clojars.org/com.lambdaisland/xml-select)
<!-- /badges -->

XPath-style selectors in Clojure

A single `select` function to conveniently search in `clojure.data.xml` style
data structures.

## Features

```clj
(require '[lambdaisland.xml-select :as xs])

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
```

<!-- installation -->
## Installation

To use the latest release, add the following to your `deps.edn` ([Clojure CLI](https://clojure.org/guides/deps_and_cli))

```
com.lambdaisland/xml-select {:mvn/version "0.1.1"}
```

or add the following to your `project.clj` ([Leiningen](https://leiningen.org/))

```
[com.lambdaisland/xml-select "0.1.1"]
```
<!-- /installation -->

## Rationale

## Usage

<!-- opencollective -->
## Lambda Island Open Source

Thank you! xml-select is made possible thanks to our generous backers. [Become a
backer on OpenCollective](https://opencollective.com/lambda-island) so that we
can continue to make xml-select better.

<a href="https://opencollective.com/lambda-island">
<img src="https://opencollective.com/lambda-island/organizations.svg?avatarHeight=46&width=800&button=false">
<img src="https://opencollective.com/lambda-island/individuals.svg?avatarHeight=46&width=800&button=false">
</a>
<img align="left" src="https://github.com/lambdaisland/open-source/raw/master/artwork/lighthouse_readme.png">

&nbsp;

xml-select is part of a growing collection of quality Clojure libraries created and maintained
by the fine folks at [Gaiwan](https://gaiwan.co).

Pay it forward by [becoming a backer on our OpenCollective](http://opencollective.com/lambda-island),
so that we continue to enjoy a thriving Clojure ecosystem.

You can find an overview of all our different projects at [lambdaisland/open-source](https://github.com/lambdaisland/open-source).

&nbsp;

&nbsp;
<!-- /opencollective -->

<!-- contributing -->
## Contributing

We warmly welcome patches to xml-select. Please keep in mind the following:

- adhere to the [LambdaIsland Clojure Style Guide](https://nextjournal.com/lambdaisland/clojure-style-guide)
- write patches that solve a problem 
- start by stating the problem, then supply a minimal solution `*`
- by contributing you agree to license your contributions as MPL 2.0
- don't break the contract with downstream consumers `**`
- don't break the tests

We would very much appreciate it if you also

- update the CHANGELOG and README
- add tests for new functionality

We recommend opening an issue first, before opening a pull request. That way we
can make sure we agree what the problem is, and discuss how best to solve it.
This is especially true if you add new dependencies, or significantly increase
the API surface. In cases like these we need to decide if these changes are in
line with the project's goals.

`*` This goes for features too, a feature needs to solve a problem. State the problem it solves first, only then move on to solving it.

`**` Projects that have a version that starts with `0.` may still see breaking changes, although we also consider the level of community adoption. The more widespread a project is, the less likely we're willing to introduce breakage. See [LambdaIsland-flavored Versioning](https://github.com/lambdaisland/open-source#lambdaisland-flavored-versioning) for more info.
<!-- /contributing -->

<!-- license -->
## License

Copyright &copy; 2024 Arne Brasseur and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license -->
