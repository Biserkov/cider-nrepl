(ns cider.nrepl.middleware.util.haystack
  "Utilities for interacting with Haystack."
  (:require
   [haystack.analyzer :as analyzer]))

(def ^:private last-analysis
  "Holds a single [exception, analysis-result] tuple.

  It's not worthwhile to cache more exception analyses,
  since normally only the latest exception is interesting."
  (atom nil))

(defn- analyze-and-cache! [e print-fn]
  (let [v (analyzer/analyze e print-fn)]
    (reset! last-analysis [e v])
    v))

(defn cached-analyze
  "Like `haystack.analyzer/analyze`, but caches the result for the last exception."
  [e print-fn]
  (if-let [[cached-e result] @last-analysis]
    (if (identical? e cached-e)
      result
      (analyze-and-cache! e print-fn))
    (analyze-and-cache! e print-fn)))
