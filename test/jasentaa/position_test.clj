(ns jasentaa.position-test
  (:require
    [clojure.test :refer :all]
    [jasentaa.position :refer :all])
  (:import
    [jasentaa.position Location]))

(deftest check-augment-then-strip
  (is (= "the quick brown fox"
     (strip-location
       (augment-location
         "the quick brown fox")))))

(deftest check-augment-location-plain-string
  (is (nil? (augment-location "")))
  (is (= (augment-location "Hello\nWorld!")
     (list
       (Location. \H 1 1 0 "Hello\nWorld!")
       (Location. \e 1 2 1 "Hello\nWorld!")
       (Location. \l 1 3 2 "Hello\nWorld!")
       (Location. \l 1 4 3 "Hello\nWorld!")
       (Location. \o 1 5 4 "Hello\nWorld!")
       (Location. \newline 1 6 5 "Hello\nWorld!")
       (Location. \W 2 1 6 "Hello\nWorld!")
       (Location. \o 2 2 7 "Hello\nWorld!")
       (Location. \r 2 3 8 "Hello\nWorld!")
       (Location. \l 2 4 9 "Hello\nWorld!")
       (Location. \d 2 5 10 "Hello\nWorld!")
       (Location. \! 2 6 11 "Hello\nWorld!")))))

(deftest check-strip-location
  (is (= \h (strip-location (Location. \h 1 1 0 "help"))))
  (is (= nil (strip-location nil)))
  (is (= "Hello" (strip-location "Hello"))))

(deftest check-exception
  (is (thrown-with-msg? java.text.ParseException
    #"Unable to parse text"
    (throw (parse-exception nil))))
  (is (thrown-with-msg? java.text.ParseException
    #"Failed to parse text at line: 6, col: 31"
    (throw (parse-exception (Location. \Y 6 31 321 "Makes no sense"))))))

(deftest check-show-error
  (let [text "We choked on street tap water well I'm gonna have to try the real thing\n
I took your laugh by the collar and it knew not to swing\n
Anytime I tried an honest job well the till had a hole and ha-ha\n
We laughed about payin' rent 'cause the county jails they're free"
        loc (vec (augment-location text))]
    (is (= (show-error (get loc 10)) "We choked on street tap water well I'm gonna have to try the real thing\n          ^\n"))
    (is (= (show-error (get loc 110)) "I took your laugh by the collar and it knew not to swing\n                                     ^\n"))
    (is (= (show-error (get loc 210)) "We laughed about payin' rent 'cause the county jails they're free\n             ^\n"))
    (is (nil? (show-error nil)))
    (is (nil? (show-error (Location. \h 1 1 1000 "wut?"))))))
