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
  ;; 2-bar pattern repeated twice, avoiding hits on bar starts
  [2 3 1/2 1 5/2 3 1/2 1 1/2])

(def snare-pattern
  (->> (phrase snare-rhythm (repeat 0))
       (all :part :snare)))

(def hihat-rhythm
  ;; Mostly eighth notes with 16th note hits on beat 7 of each bar
  [1/2 1/2 1/2 1/2 1/2 1/2 1/4 1/4   ; bar 1 (7 eighth notes = 3.5 beats)
   1/2 1/2 1/2 1/2 1/2 1/2 1/4 1/4   ; bar 2
   1/2 1/2 1/2 1/2 1/2 1/2 1/4 1/4   ; bar 3
   1/2 1/2 1/2 1/2 1/2 1/2 1/4 1/4]) ; bar 4

(def hihat-pattern
  (->> (phrase hihat-rhythm (repeat 0))
       (all :part :hihat)))

(def melody-rhythm
  ;; Emphasizing 2+2+3 pattern with accented notes on strong beats
  [1 1 1/2 1/2 1 1 1/2 3/2 1 1 1/2 1/2 1 1 1/2 1 1/2])

(def melody-pitches
  ;; Busier line with accents on 2+2+3 beats
  [5 4 5 6 8 7 8 8 7 6 5 6 7 6 7 6 4])

(def melody-line
  (->> (phrase melody-rhythm melody-pitches)
       (where :pitch scale/raise)
       (all :part :melody)))

(def texture-rhythm [6 8])

(def texture-pattern
  (->> (phrase texture-rhythm (repeat 0))
       (all :part :texture)))

(def chords
  (->> (phrase harmonic-rhythm chord-progression)
       (where :pitch scale/lower)
       (all :part :chords)))

(def a-section
  (->> chords
       (with bass-line)
       (with melody-line)
       (with kick-pattern)
       (with snare-pattern)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; Alternate melody for B section
(def melody-b-rhythm
  ;; Different rhythmic pattern, still emphasizing 7/8 feel
  [1/2 1/2 1 1/2 1/2 1 1 1/2 1/2 1 1 1/2 1/2 1 1 1/2 1/2])

(def melody-b-pitches
  ;; Contrasting melodic contour, exploring different scale degrees
  [3 4 6 5 4 6 7 8 7 6 8 7 6 5 6 4 3])

(def melody-line-b
  (->> (phrase melody-b-rhythm melody-b-pitches)
       (where :pitch scale/raise)
       (all :part :melody)))

(def b-section
  (->> chords
       (with bass-line)
       (with melody-line-b)
       (with texture-pattern)
       (with kick-pattern)
       (with snare-pattern)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; Bridge section - halftime, 4/4, reggae feel
(def bridge-chord-progression
  [(-> chord/triad (chord/root 1))   ; F
   (-> chord/triad (chord/root 5))   ; C
   (-> chord/triad (chord/root 4))   ; Bb
   (-> chord/triad (chord/root 4))]) ; Bb

(def bridge-harmonic-rhythm
  ;; 4 bars of 4/4, each chord gets 1 bar (4 beats)
  [4 4 4 4])

(def bridge-stabs-rhythm
  ;; Reggae stabs on offbeats (2 and 4 of each bar)
  ;; Pattern: rest-stab-rest-stab for each bar
  [1 1/2 1/2 1 1/2 1/2   ; bar 1: F
   1 1/2 1/2 1 1/2 1/2   ; bar 2: C
   1 1/2 1/2 1 1/2 1/2   ; bar 3: Bb
   1 1/2 1/2 1 1/2 1/2]) ; bar 4: Bb

(def bridge-stabs-pitches
  ;; Voicing for reggae stabs (5th and 3rd of each chord)
  [5 3 5 3   ; F chord (C, A)
   5 3 5 3   ; C chord (G, E)
   4 2 4 2   ; Bb chord (F, D)
   4 2 4 2]) ; Bb chord (F, D)

(def bridge-stabs
  (->> (phrase bridge-stabs-rhythm bridge-stabs-pitches)
       (where :pitch scale/raise)
       (all :part :bridge-stabs)))

(def bridge-bass-rhythm
  ;; Simple bass on 1 and 3 of each bar (one drop feel)
  [2 2 2 2 2 2 2 2])

(def bridge-bass-pitches
  ;; Root notes: F, C, Bb, Bb
  [1 1 5 5 4 4 4 4])

(def bridge-bass-line
  (->> (phrase bridge-bass-rhythm bridge-bass-pitches)
       (where :pitch (comp scale/lower scale/lower scale/lower))
       (all :part :bridge-bass)))

(def bridge-melody-rhythm
  ;; Blues-inflected melody with syncopation
  [1 1/2 1/2 1 1 1/2 1/2 1 1/2 1/2 1 1 1 1/2 1/2])

(def bridge-melody-pitches
  ;; Blues scale using fractional degrees: 1.5 = blue third, 4.5 = blue seventh
  [1 1.5 1.5 2 1 4.5 4.5 5 4 4 1.5 1.5 5 4 1.5 1])

(def bridge-melody
  (->> (phrase bridge-melody-rhythm bridge-melody-pitches)
       (all :part :bridge-melody)))

(def bridge-kick-rhythm
  ;; One drop: kick on 3 of each bar
  [2 2 2 2 2 2 2 2])

(def bridge-kick-pattern
  (->> (phrase bridge-kick-rhythm (repeat 0))
       (all :part :kick)))

(def bridge-snare-rhythm
  ;; Snare on 2 and 4
  [1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1])

(def bridge-snare-pattern
  (->> (phrase bridge-snare-rhythm (repeat 0))
       (all :part :bridge-snare)))

(def bridge
  (->> bridge-stabs
       (with bridge-bass-line)
       (with bridge-melody)
       (with bridge-kick-pattern)
       (with bridge-snare-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 60))))

;; Full arrangement: b, a, b, a, bridge (2x) -> repeat
(def full-arrangement
  (->> b-section
       (then a-section)
       (then b-section)
       (then a-section)
       (then (times 2 bridge))))

;; Main song var - update this to change what's playing
(def winter-water full-arrangement)

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

(definst breathy-lead [freq 440 dur 1.0 volume 0.4]
  (-> (saw freq)
      (+ (* 0.3 (saw (* freq 1.01))))
      (+ (* 0.2 (white-noise)))
      (lpf (+ freq (* 800 (env-gen (perc 0.3 0.7)))))
      (rlpf (* freq 3) 0.3)
      (* volume)
      (* (env-gen (adsr 0.15 0.3 0.6 0.4) (line:kr 1 0 dur) :action FREE))))

(definst ambient-texture [freq 200 dur 4.0 volume 0.15]
  (let [noise (pink-noise)
        random-lfo (lf-noise1:kr 0.3)
        random-lfo2 (lf-noise1:kr 0.17)
        filter-freq (+ 300 (* 1800 (+ 0.5 (* 0.5 random-lfo))))
        resonance (+ 0.2 (* 0.3 (+ 0.5 (* 0.5 random-lfo2))))]
    (-> noise
        (rlpf filter-freq resonance)
        (rlpf filter-freq resonance)
        (* volume)
        (* (env-gen (adsr 1.5 0.5 0.7 2.0) (line:kr 1 0 dur) :action FREE)))))

(definst reggae-stabs [freq 440 dur 0.5 volume 0.6]
  (-> (saw freq)
      (+ (* 0.5 (saw (* freq 1.01))))
      (lpf 1200)
      (rlpf 800 0.5)
      (* volume)
      (* (env-gen (perc 0.001 0.12) (line:kr 1 0 dur) :action FREE))))

(defmethod live/play-note :default
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (bass freq seconds :volume 1.5)))

(defmethod live/play-note :chords
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
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

(defmethod live/play-note :melody
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (breathy-lead freq seconds :volume 0.35)))

(defmethod live/play-note :texture
  [{midi :pitch seconds :duration}]
  (ambient-texture 200 seconds :volume 0.18))

(defmethod live/play-note :bridge-stabs
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (reggae-stabs freq seconds :volume 0.5)))

(defmethod live/play-note :bridge-bass
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (bass freq seconds :volume 1.2)))

(defmethod live/play-note :bridge-melody
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (breathy-lead freq seconds :volume 0.4)))

(defmethod live/play-note :bridge-snare
  [{midi :pitch seconds :duration}]
  (snare 200 seconds :volume 0.5))

(comment
 (->> winter-water var live/jam)
 (->> a-section var live/jam)
 (->> b-section var live/jam)
 (->> bridge var live/jam)
 (live/stop)
)

(defn -main
  [& args]
  (println "Starting Winter Water...")
  (->> winter-water (take 1) live/play))
