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

;; Main A section - full arrangement (no texture)
(def a-section
  (->> chords
       (with bass-line)
       (with melody-line)
       (with kick-pattern)
       (with snare-pattern)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; Melody pad line (same as melody but sustained)
(def melody-pad-line
  (->> (phrase melody-rhythm melody-pitches)
       (where :pitch scale/raise)
       (all :part :melody-pad)))

;; A section with doubled melody (plucky + sustained)
(def a-section-doubled
  (->> chords
       (with bass-line)
       (with melody-line)
       (with melody-pad-line)
       (with texture-pattern)
       (with kick-pattern)
       (with snare-pattern)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; Texture that builds over time - multiple layers with increasing volume
(def texture-build
  (->> (phrase [7 7 7 7] [0 0 0 0]) ; 4 layers across the double-chorus
       (map-indexed (fn [idx note]
                      (assoc note :volume (* 0.15 (+ 1 (* 0.5 idx))))))
       (all :part :texture)))

;; Double-chorus - maxed out A section after bridge (4x for double length)
(def double-chorus
  (->> (times 4 a-section-doubled)
       (with texture-build)))

;; Outro - just whooshing texture for one bar
(def outro
  (->> (phrase [7/2] [0])
       (all :part :texture)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; B section - sparse, contrasting arrangement
(def b-bass-rhythm
  ;; Sparser bass hits
  [7/2 7/2 7/2 7/2])

(def b-bass-pitches
  [3 4 2 5])

(def b-bass-line
  (->> (phrase b-bass-rhythm b-bass-pitches)
       (where :pitch (comp scale/lower scale/lower scale/lower))))

;; Power chords for b-section (root and fifth only)
(def b-power-chords-rhythm
  [7/2 7/2 7/2 7/2])

(def b-power-chords
  [[[0 3] [0 4]]  ; F power chord (root on 3rd degree, fifth)
   [[0 4] [0 5]]  ; G power chord (root on 4th degree, fifth)
   [[0 2] [0 3]]  ; D power chord (root on 2nd degree, fifth)
   [[0 5] [0 6]]]) ; C power chord (root on 5th degree, fifth)

(def b-chord-line
  (->> (phrase b-power-chords-rhythm b-power-chords)
       (where :pitch scale/lower)
       (all :part :chords)))

(def b-melody-rhythm
  ;; More space between phrases
  [1 1 1 1/2 3 1/2 1 1 1 1/2 3 1/2])

(def b-melody-pitches
  ;; Simpler, more melodic line
  [8 7 6 5 4 3 6 5 4 3 2 1])

(def b-melody-line
  (->> (phrase b-melody-rhythm b-melody-pitches)
       (where :pitch scale/raise)
       (all :part :melody)))

(def b-section
  (->> b-bass-line
       (with b-melody-line)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; B melody harmony - delayed by 1 bar (7/2 beats in 7/8 time) in sixths
;; Trimmed to fit within the section duration
(def b-melody-sixths
  ;; Create chord pairs: original note + third below (2 scale degrees)
  (map (fn [p] [p (- p 2)]) b-melody-pitches))

(def b-melody-harmony
  (->> (phrase b-melody-rhythm b-melody-sixths)
       (take-while (fn [note] (< (:time note) (- 14 7/2)))) ; only notes that fit before section ends
       (after 7/2) ; delay by 1 bar (3.5 beats)
       (where :pitch scale/raise)
       (all :part :melody-pad)))

;; B section with harmony
(def b-section-harmony
  (->> b-bass-line
       (with b-melody-line)
       (with b-melody-harmony)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; C section - power chords with melody (no bass), also uses shortened intro melody
(def c-section
  (->> b-chord-line
       (with c-intro-melody-line)
       (with hihat-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; C section intro melody - just first phrase (no reply)
(def c-intro-melody-rhythm
  [1 1 1 1/2 3])

(def c-intro-melody-pitches
  [8 7 6 5 4])

(def c-intro-melody-line
  (->> (phrase c-intro-melody-rhythm c-intro-melody-pitches)
       (where :pitch scale/raise)
       (all :part :melody)))

;; C section intro - just chords and melody, no drums
(def c-section-intro
  (->> b-chord-line
       (with c-intro-melody-line)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 120))))

;; C section reprise - with alternating Bb/C bass (continuous cycling)
(def c-reprise-bass-rhythm
  [7/2 7/2 7/2 7/2]) ; one bar each, fills all 4 bars

(def c-reprise-bass-pitches
  [4 5 4 5]) ; Bb, C, Bb, C - continuous cycle

(def c-reprise-bass-line
  (->> (phrase c-reprise-bass-rhythm c-reprise-bass-pitches)
       (where :pitch (comp scale/lower scale/lower scale/lower))))

(def c-section-reprise
  (->> b-chord-line
       (with c-reprise-bass-line)
       (with b-melody-line)
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
   5 3 2 3   ; C chord (G, E) - 7th note goes down
   4 2 4 2   ; Bb chord (F, D)
   4 2 4 2]) ; Bb chord (F, D)

(def bridge-stabs
  (->> (phrase bridge-stabs-rhythm bridge-stabs-pitches)
       (where :pitch scale/raise)
       (all :part :bridge-stabs)))

;; Organ stabs for bridge (same rhythm as synth stabs, higher octave)
(def bridge-organ-stabs
  (->> (phrase bridge-stabs-rhythm bridge-stabs-pitches)
       (where :pitch scale/raise)
       (where :pitch scale/raise) ; raise twice for higher octave
       (all :part :bridge-organ-stabs)))

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

;; Tictoc pattern - alternating on half beats (like a clock)
(def bridge-tictoc-rhythm
  (repeat 32 1/2)) ; 16 beats = 32 half beats

(def bridge-tictoc-pitches
  (cycle [0 1])) ; alternating tick and toc

(def bridge-tictoc-pattern
  (->> (phrase bridge-tictoc-rhythm bridge-tictoc-pitches)
       (all :part :tictoc)))

(def bridge
  (->> bridge-stabs
       (with bridge-organ-stabs)
       (with bridge-bass-line)
       (with bridge-melody)
       (with bridge-kick-pattern)
       (with bridge-snare-pattern)
       (with bridge-tictoc-pattern)
       (where :pitch (comp scale/F scale/major))
       (tempo (bpm 60))))

;; Full arrangement: c intro (no drums), c (1x with drums), a (2x), b (2x), a-doubled (2x), b-harmony (2x), c-reprise (2x), bridge (2x), double-chorus, outro -> repeat
(def full-arrangement
  (->> c-section-intro
       (then c-section)
       (then (times 2 a-section))
       (then (times 2 b-section))
       (then (times 2 a-section-doubled))
       (then (times 2 b-section-harmony))
       (then (times 2 c-section-reprise))
       (then (times 2 bridge))
       (then double-chorus)
       (then outro)))

;; Main song var - update this to change what's playing
(def winter-water full-arrangement)

(definst bass [freq 110 dur 1.0 res 1000 volume 1.0 pan 0]
  (let [random-lfo (lf-noise1:kr 1.0)
        filter-freq (+ 400 (* 600 (+ 0.5 (* 0.5 random-lfo))))]
    (-> (sin-osc freq)
        (+ (* 1/3 (sin-osc (* 2 freq))))
        (+ (* 1/2 (sin-osc (* 3 freq))))
        (+ (* 1/3 (sin-osc (* 5 freq))))
        (* (square 2))
        (* 3.0) ; boost before clipping for heavy overdrive
        (clip2 0.4) ; lower threshold for more aggressive clipping
        (* 1.5) ; make up gain after clipping
        (rlpf filter-freq 0.5) ; wobbling resonant low pass filter
        (* volume)
        (* (env-gen (adsr 0.01 0.2 0.3 0.1) (line:kr 1 0 dur) :action FREE))
        (pan2 pan))))

(definst organ [freq 110 dur 1.0 res 1000 volume 1.0 pan 0]
  (-> (sin-osc freq)
      (+ (* 1/3 (sin-osc (* 2 freq))))
      (+ (* 1/2 (sin-osc (* 4 freq))))
      (* 9)
      (clip2 (line:kr 0.6 0.1 3.5))
      (rlpf (line:kr 2000 100 3.5) 0.8)
      (* volume)
      (* (env-gen (adsr 0.04 0.1 0.5 0.1) (line:kr 1 0 dur) :action FREE))
      (free-verb 0.4 0.7 0.5)
      (pan2 pan)))

(definst kick [freq 60 dur 0.5 volume 1.0 pan 0]
  (let [dry-sig (-> (sin-osc (line:kr freq 40 0.1))
                    (+ (* 0.5 (white-noise)))
                    (lpf 200)
                    (* volume)
                    (* (env-gen (perc 0.001 0.3) (line:kr 1 0 dur) :action FREE)))
        verb-sig (-> dry-sig
                     (hpf 100) ; high-pass before reverb to keep low end tight
                     (free-verb 0.4 0.9 0.3))]
    (-> (+ (* 0.7 dry-sig) (* 0.3 verb-sig))
        (pan2 pan))))

(definst snare [freq 200 dur 0.5 volume 0.8 pan 0]
  (let [pitch-env (env-gen (perc 0.001 0.05) :level-scale 80)
        tone-freq (+ 180 pitch-env)]
    (-> (white-noise)
        (+ (* 0.4 (sin-osc tone-freq))) ; pitch-modulated tone
        (+ (* 0.2 (pink-noise))) ; add pink noise for body
        (hpf 300)
        (bpf 2500 0.8) ; add resonant peak
        (lpf 8000)
        (* 1.3) ; boost
        (clip2 0.7) ; add some grit
        (* volume)
        (* (env-gen (perc 0.001 0.18) (line:kr 1 0 dur) :action FREE))
        (free-verb 0.35 0.6 0.3)
        (pan2 pan))))

(definst hihat [freq 8000 dur 0.5 volume 0.3 pan 0]
  (-> (white-noise)
      (hpf 6000)
      (lpf 12000)
      (* volume)
      (* (env-gen (perc 0.001 0.08) (line:kr 1 0 dur) :action FREE))
      (free-verb 0.2 0.4 0.2)
      (pan2 pan)))

(definst breathy-lead [freq 440 dur 1.0 volume 0.4 pan 0]
  (-> (saw freq)
      (+ (* 0.3 (saw (* freq 1.01))))
      (+ (* 0.2 (white-noise)))
      (lpf (+ freq (* 800 (env-gen (perc 0.001 0.3)))))
      (rlpf (* freq 3) 0.3)
      (* volume)
      (* (env-gen (perc 0.001 0.5) (line:kr 1 0 dur) :action FREE))
      (free-verb 0.6 0.8 0.5)
      (pan2 pan)))

(definst breathy-pad [freq 440 dur 1.0 volume 0.3 pan 0]
  (-> (saw freq)
      (+ (* 0.3 (saw (* freq 1.01))))
      (+ (* 0.2 (white-noise)))
      (lpf (+ freq (* 800 (env-gen (perc 0.3 0.7)))))
      (rlpf (* freq 3) 0.3)
      (* volume)
      (* (env-gen (adsr 0.15 0.3 0.6 0.4) (line:kr 1 0 dur) :action FREE))
      (free-verb 0.6 0.8 0.5)
      (pan2 pan)))

(definst ambient-texture [freq 200 dur 4.0 volume 0.15 pan 0]
  (let [noise (pink-noise)
        random-lfo (lf-noise1:kr 0.3)
        random-lfo2 (lf-noise1:kr 0.17)
        filter-freq (+ 300 (* 1800 (+ 0.5 (* 0.5 random-lfo))))
        resonance (+ 0.2 (* 0.3 (+ 0.5 (* 0.5 random-lfo2))))]
    (-> noise
        (rlpf filter-freq resonance)
        (rlpf filter-freq resonance)
        (* volume)
        (* (env-gen (adsr 1.5 0.5 0.7 2.0) (line:kr 1 0 dur) :action FREE))
        (pan2 pan))))

(definst reggae-stabs [freq 440 dur 0.5 volume 0.6 pan 0]
  (-> (saw freq)
      (+ (* 0.5 (saw (* freq 1.01))))
      (lpf 1200)
      (rlpf 800 0.5)
      (* volume)
      (* (env-gen (perc 0.001 0.12) (line:kr 1 0 dur) :action FREE))
      (pan2 pan)))

(definst tictoc [pitch 0 dur 0.1 volume 0.4 pan 0]
  (-> (sin-osc (+ 1200 (* pitch 400))) ; alternating high/low pitches
      (* volume)
      (* (env-gen (perc 0.001 0.03) (line:kr 1 0 dur) :action FREE))
      (pan2 pan)))

(defmethod live/play-note :default
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (bass freq seconds :volume 1.2 :pan 0)))

(defmethod live/play-note :chords
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (organ freq seconds :volume 0.2 :pan (- 0.2 (* 0.4 (rand))))))

(defmethod live/play-note :kick
  [{midi :pitch seconds :duration}]
  (kick 60 seconds :volume 1.0 :pan 0))

(defmethod live/play-note :snare
  [{midi :pitch seconds :duration}]
  (snare 200 seconds :volume 0.6 :pan 0.1))

(defmethod live/play-note :hihat
  [{midi :pitch seconds :duration}]
  (hihat 8000 seconds :volume 0.25 :pan 0.3))

(defmethod live/play-note :melody
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (breathy-lead freq seconds :volume 0.8 :pan -0.1)))

(defmethod live/play-note :melody-pad
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (breathy-pad freq seconds :volume 0.35 :pan 0.4)))

(defmethod live/play-note :texture
  [{midi :pitch seconds :duration :as note}]
  (let [vol (or (:volume note) 0.18)]
    (ambient-texture 200 seconds :volume vol :pan (- 0.5 (rand)))))

(defmethod live/play-note :bridge-stabs
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (reggae-stabs freq seconds :volume 0.5 :pan (- 0.3 (* 0.6 (rand))))))

(defmethod live/play-note :bridge-organ-stabs
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (organ freq seconds :volume 0.15 :pan (- 0.2 (* 0.4 (rand))))))

(defmethod live/play-note :bridge-bass
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (bass freq seconds :volume 1.0 :pan 0)))

(defmethod live/play-note :bridge-melody
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (breathy-lead freq seconds :volume 0.8 :pan 0)))

(defmethod live/play-note :bridge-snare
  [{midi :pitch seconds :duration}]
  (snare 200 seconds :volume 0.5 :pan 0.1))

(defmethod live/play-note :tictoc
  [{midi :pitch seconds :duration}]
  (tictoc midi seconds :volume 0.35 :pan -0.7))

(comment
 (->> winter-water var live/jam)
 (->> c-section var live/jam)
 (->> a-section var live/jam)
 (->> b-section var live/jam)
 (->> bridge var live/jam)
 (live/stop)

 ;; Recording
 (recording-start "~/Desktop/winter-water.wav")
 (->> winter-water live/play)
 ;; Wait for track to finish, then:
 (recording-stop)
)

(defn -main
  [& args]
  (println "Starting Winter Water...")
  (->> winter-water (take 1) live/play))
