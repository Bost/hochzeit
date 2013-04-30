(ns hochzeit.email
  (:require [postal.core :as post]))

(post/send-message ^{:host "smtp.gmail.com"
                             :user "thebost@gmail.com"
                             :pass ">>> app-specific password <<<"
                             :ssl :yes!!!11}
                           {:from "thebost@gmail.com"
                            :to "thebost@gmail.com"
                            :subject "[hochzeit]"
                            :body "Some text"})
