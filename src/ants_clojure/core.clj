(ns ants-clojure.core
  (:require [clojure.java.io :refer [resource]])
  (:import [javafx.application Application]
           [javafx.fxml FXMLLoader]
           [javafx.stage Stage]
           [javafx.scene Scene]
           [javafx.animation AnimationTimer]
           (javafx.scene.paint Color))
  (:gen-class :extends javafx.application.Application))

(def width 800)
(def height 600)
(def ant-count 100)
(def ants (atom nil))                                       ;allows to create a mutable. global container to hold andts
(def last-timestamp (atom 0))
(defn create-ants []
  (for [i (range 0 ant-count)]
    {:x (rand-int width)
     :y (rand-int height)}))

(defn random-step []
  (- (* 2 (rand)) 1))

(defn move-ant [ant]                                        ;maping over ants to move them
  (Thread/sleep 5)
  (assoc ant :x (+ (random-step) (:x ant))
             :y (+ (random-step) (:y ant))))

(defn draw-ants [context]
  (.clearRect context 0 0 width height)                     ;clears the screen
  (doseq [ant (deref ants)]
    (.setFill context Color/BLACK)
    (.fillOval context (:x ant ) (:y ant) 5 5)))

(defn fps [now]
  (let [diff (- now (deref last-timestamp))
        diff-seconds (/ diff 1000000000)]
    (int (/ 1 diff-seconds))))

(defn -start [app ^Stage stage]
  (let [root (FXMLLoader/load (resource "main.fxml"))
        scene (Scene. root width height)
        canvas (.lookup scene "#canvas")                    ;draw on
        fps-label (.lookup scene "#fps")                    ;displays frams per sec
        context (.getGraphicsContext2D canvas)
        timer (proxy [AnimationTimer] []
                (handle [now]
                  (.setText fps-label (str (fps now)))
                  (reset! last-timestamp now)
                  (reset! ants (pmap move-ant (deref ants)))
                  (draw-ants context)))]
    (reset! ants (create-ants))
    (doto stage                                             ;createing the window
      (.setTitle "Ants")
      (.setScene scene)
      (.show))
    (.start timer)))

(defn -main [& args]
  (Application/launch ants_clojure.core (into-array String args)))
