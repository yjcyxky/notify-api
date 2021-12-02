(defproject org.clojars.yjcyxky/notify-api "0.1.0"
  :description "Notify someone with something by email, dingtalk etc."
  :url "https://github.com//"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.9.1"]
                 [lambdaisland/uri "1.2.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/tools.logging "0.5.0"
                  :exclusions [org.clojure/clojure]]]
  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]
            [lein-changelog "0.3.2"]
            [lein-codox "0.10.8"]]
  :codox {:metadata {:doc/format :markdown}
          :source-uri "https://github.com/yjcyxky/notify-api/blob/{version}/{filepath}#L{line}"}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0"]]}}
  :deploy-repositories [["releases" :clojars]]
  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[org\\.clojars\\.yjcyxky\\\\/notify-api \"[0-9.]*\"\\\\]/[org\\.clojars\\.yjcyxky\\\\/notify-api \"${:version}\"]/" "README.md"]}
  :release-tasks [["shell" "git" "diff" "--exit-code"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["changelog" "release"]
                  ["update-readme-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["vcs" "push"]])
