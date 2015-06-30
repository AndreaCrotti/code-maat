(ns code-maat.parsers.detect
  (:require [me.raynes.conch :refer [with-programs let-programs] :as sh]))

(def git-options
  "--all -M -C --numstat --date=short --pretty=format:'--%h--%cd--%cn'")

(comment "generate the date information also from the cli")
(def hg-option
  "log --template 'rev: {rev} author: {author} date: {date|shortdate} files:\n{files %'{file}\n'}\n' --date '>YYYY-MM-DD")

(def vc-flag
  {".git" :git
   ".hg"  :mercurial
   ".svn" :subversion})

;TODO: 
(def vc-options
  {:git       "log --all -M -C --numstat --date=short --pretty=format:'--%h--%cd--%cn'"
   :mercurial "log --template 'rev: {rev} author: {author} date: {date|shortdate} files:\n{files %'{file}\n'}\n' --date '>YYYY-MM-DD"
   :subversion "log -v --xml > logfile.log -r {YYYYmmDD}:HEAD"})


(defn- detect
  [path flag]
  (let [fullpath (str path "/" flag)] ;TODO: clearly not sufficiently smart
    (when (->> fullpath
               clojure.java.io/as-file
               .exists)

      (get vc-flag flag))))

(defn detect-vc-type
  [path]
  (let [found (map #(detect path %) (keys vc-flag))
        non-nils (filter #(not (nil? %)) found)
        res-size (count non-nils)]
    (comment "Do something meaningful here")
    (cond
      (= 0 res-size) (print "Error nothing found!")
      (> res-size 2) (print "Too many found")
      :else (first non-nils))))
