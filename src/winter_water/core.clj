(ns winter-water.core
  (:require 
    [overtone.core :refer :all :exclude [stop sharp flat]]
    [leipzig.melody :refer :all]
    [leipzig.scale :as scale]
    [leipzig.chord :as chord]
    [leipzig.live :as live]
    [leipzig.temperament :as temperament]
    [overtone.inst.synth :as synth]))

(when-not (server-connected?)
  (connect-server "127.0.0.1" 57110))

(def chord-progression
  [(-> chord/triad (chord/root 3) (chord/inversion 2))
   (-> chord/triad (chord/root 3) (chord/inversion 2))
   (-> chord/triad (chord/root 4) (chord/inversion 2))
   (-> chord/triad (chord/root 6) (chord/inversion 1))
   (-> chord/triad (chord/root 2))
   (-> chord/triad (chord/root 2))
   (-> chord/triad (chord/root 5) (chord/inversion 2))
   (-> chord/triad (chord/root 4) (chord/inversion 2))])

(def harmonic-rhythm [6/2 1/2 4/2 3/2 6/2 1/2 4/2 3/2])

(def chord-roots [3 4 3 2 5 4])

(def bass-rhythm [7/2 4/2 2/2 1/2 7/2 4/2 1 1/2])

(def bass-pitches [3 4 3 3 2 5 4 1])

(def bass-line
  (->> (phrase bass-rhythm bass-pitches)
       (where :pitch (comp scale/lower scale/lower scale/lower))))

;; Drum patterns for 7/8 time (2+2+3 grouping)
;; 4 bars = 28 eighth notes = 14 beats total
(def kick-rhythm
  ;; Hits on beats 1, 3, 5 of each 7/8 bar (2+2+3 pattern)
  [1 1 3/2 1 1 3/2 1 1 3/2 1 1 3/2])

(def kick-pattern
  (->> (phrase kick-rhythm (repeat 0))
       (all :part :kick)))

(def snare-rhythm
  ;; Backbeat on beat 4 of each bar
  [3/2 2 3/2 2 3/2 2 3/2 2])

(def snare-pattern
  (->> (phrase snare-rhythm (repeat 0))
       (all :part :snare)))

(def hihat-rhythm
  ;; Steady eighth notes - 28 notes for 4 bars of 7/8
  (repeat 28 1/2))

(def hihat-pattern
  (->> (phrase hihat-rhythm (repeat 0))
       (all :part :hihat)))

(def chords
  (->> (phrase harmonic-rhythm chord-progression)
       (where :pitch scale/lower)
       (all :part :chords)))

(def winter-water
  (->> chords
       (with bass-line)
       (with kick-pattern)
       (with snare-pattern)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

(definst bass [freq 110 dur 1.0 res 1000 volume 1.0]
  (-> (sin-osc freq)
      (+ (* 1/3 (sin-osc (* 2 freq))))
      (+ (* 1/2 (sin-osc (* 3 freq))))
      (+ (* 1/3 (sin-osc (* 5 freq))))
      (* (square 2))
      (clip2 0.6)
      (* volume)
      (* (env-gen (adsr 0.01 0.2 0.3 0.1) (line:kr 1 0 dur) :action FREE))))

(definst organ [freq 110 dur 1.0 res 1000 volume 1.0]
  (-> (sin-osc freq)
      (+ (* 1/3 (sin-osc (* 2 freq))))
      (+ (* 1/2 (sin-osc (* 4 freq))))
      (* 9)
      (clip2 (line:kr 0.6 0.1 3.5))
      (rlpf (line:kr 2000 100 3.5) 0.8)
      (* volume)
      (* (env-gen (adsr 0.04 0.1 0.5 0.1) (line:kr 1 0 dur) :action FREE))))

(definst kick [freq 60 dur 0.5 volume 1.0]
  (-> (sin-osc (line:kr freq 40 0.1))
      (+ (* 0.5 (white-noise)))
      (lpf 200)
      (* volume)
      (* (env-gen (perc 0.001 0.3) (line:kr 1 0 dur) :action FREE))))

(definst snare [freq 200 dur 0.5 volume 0.8]
  (-> (white-noise)
      (+ (* 0.3 (sin-osc 180)))
      (hpf 300)
      (lpf 8000)
      (* volume)
      (* (env-gen (perc 0.001 0.15) (line:kr 1 0 dur) :action FREE))))

(definst hihat [freq 8000 dur 0.5 volume 0.3]
  (-> (white-noise)
      (hpf 6000)
      (lpf 12000)
      (* volume)
      (* (env-gen (perc 0.001 0.08) (line:kr 1 0 dur) :action FREE))))

(defmethod live/play-note :default
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (bass freq seconds :volume 1.5)))

(defmethod live/play-note :chords
  [{midi :pitch seconds :duration}]
  ;; Suppressed temporarily
  nil
  #_(let [freq (midi->hz midi)]
    (organ freq seconds :volume 0.2)))

(defmethod live/play-note :kick
  [{midi :pitch seconds :duration}]
  (kick 60 seconds :volume 1.0))

(defmethod live/play-note :snare
  [{midi :pitch seconds :duration}]
  (snare 200 seconds :volume 0.6))

(defmethod live/play-note :hihat
  [{midi :pitch seconds :duration}]
  (hihat 8000 seconds :volume 0.25))

(comment
 (->> winter-water var live/jam) 
 (->> winter-water live/play) 
 (live/stop)
)

(defn -main
  [& args]
  (println "Starting Winter Water...")
  (->> winter-water (take 1) live/play))
