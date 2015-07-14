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
(def vc-config
  {:git       {:cmd "git"
               :options "log --all -M -C --numstat --date=short --pretty=format:'--%h--%cd--%cn'"}
   :mercurial {:cmd "hg"
               :options "log --template 'rev: {rev} author: {author} date: {date|shortdate} files:\n{files %'{file}\n'}\n' --date '>YYYY-MM-DD"}
   :subversion {:cmd "svn"
                :options "log -v --xml > logfile.log -r {YYYYmmDD}:HEAD"}})

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

(defn get-log
  [path]
  (let [vc-used (detect-vc-type path)
        vc-conf (vc-used vc-config)
        cmd (:cmd vc-conf)]
    (with-programs [cmd]
      (cmd (:options vc-conf)))))
