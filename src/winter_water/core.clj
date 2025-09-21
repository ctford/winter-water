(ns winter-water.core
  (:require [leipzig.melody :refer :all]
            [leipzig.scale :as scale]
            [leipzig.chord :as chord]
            [leipzig.live :as live]
            [leipzig.temperament :as temperament]
            [overtone.core :as overtone]
            [overtone.inst.synth :as synth]))

(defn boot-server! []
  (try
    (overtone/boot-internal-server)
    (println "SuperCollider server booted successfully!")
    (catch Exception e
      (println "Failed to boot server:" (.getMessage e)))))

(defn note [time duration pitch]
  {:time time :duration duration :pitch pitch})

(def chord-progression
  {:bb (chord/triad :Bb :major)
   :c (chord/triad :C :major)
   :edim-bb (chord/triad :E :diminished)
   :am (chord/triad :A :minor)
   :dm (chord/triad :D :minor)})

(def root-notes
  {:bb :Bb
   :c :C
   :edim-bb :Bb
   :am :A
   :dm :D})

(def bar-duration 7/8)
(def eighth-note 1/8)

(defn create-chord-sequence []
  (let [bb-bar (note 0 bar-duration (:bb chord-progression))
        c-edim-bar [(note bar-duration (* 4 eighth-note) (:c chord-progression))
                    (note (+ bar-duration (* 4 eighth-note)) (* 3 eighth-note) (:edim-bb chord-progression))]
        am-bar (note (* 2 bar-duration) bar-duration (:am chord-progression))
        dm-c-bar [(note (* 3 bar-duration) (* 4 eighth-note) (:dm chord-progression))
                  (note (+ (* 3 bar-duration) (* 4 eighth-note)) (* 3 eighth-note) (:c chord-progression))]]
    (concat [bb-bar] c-edim-bar [am-bar] dm-c-bar)))

(defn create-bass-line []
  (let [bb-note (note 0 bar-duration (:bb root-notes))
        c-note (note bar-duration (* 4 eighth-note) (:c root-notes))
        bb-note-2 (note (+ bar-duration (* 4 eighth-note)) (* 3 eighth-note) (:edim-bb root-notes))
        am-note (note (* 2 bar-duration) bar-duration (:am root-notes))
        dm-note (note (* 3 bar-duration) (* 4 eighth-note) (:dm root-notes))
        c-note-2 (note (+ (* 3 bar-duration) (* 4 eighth-note)) (* 3 eighth-note) (:c root-notes))]
    [bb-note c-note bb-note-2 am-note dm-note c-note-2]))

(defn transpose-bass [bass-line octave-offset]
  (->> bass-line
       (map #(update % :pitch + (* 12 octave-offset)))))

(defn create-organ-chords []
  (create-chord-sequence))

(def bass-part
  (->> (create-bass-line)
       (transpose-bass -2)
       (where :part (is :bass))))

(def organ-part
  (->> (create-organ-chords)
       (where :part (is :organ))))

(def winter-water-phrase
  (->> (concat bass-part organ-part)
       (tempo (bpm 120))))

(defn play-winter-water []
  (live/play winter-water-phrase))

(defn loop-winter-water [times]
  (let [phrase-duration (* 4 bar-duration)
        looped-phrase (->> (range times)
                          (mapcat #(->> winter-water-phrase
                                       (after (* % phrase-duration)))))]
    (live/play looped-phrase)))

(defn -main
  [& args]
  (println "Starting Winter Water...")
  (play-winter-water))