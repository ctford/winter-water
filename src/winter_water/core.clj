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
  (boot-internal-server {:num-input-bus-channels 0 
                         :input-device-id -1}))

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

(def chords
  (->> (phrase harmonic-rhythm chord-progression)
       (where :pitch scale/lower)
       (all :part :chords)))

(def winter-water
  (->> chords
       (with bass-line)
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

(defmethod live/play-note :default
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (bass freq seconds :volume 1.5)))

(defmethod live/play-note :chords
  [{midi :pitch seconds :duration}]
  (let [freq (midi->hz midi)]
    (organ freq seconds :volume 0.2)))

(comment
 (->> winter-water var live/jam) 
 (->> winter-water live/play) 
 (live/stop)
)

(defn -main
  [& args]
  (println "Starting Winter Water...")
  (->> winter-water (take 1) live/play))
