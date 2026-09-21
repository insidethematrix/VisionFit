package com.example.visionfit.analyzer;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class SquatAnalyzer {

    // Thresholds for knee angles
    private static final double SQUAT_DOWN_THRESHOLD = 90.0; // The angle when they are considered "down" in a squat
    private static final double SQUAT_UP_THRESHOLD = 160.0;  // The angle when they are standing straight

    private boolean isDown = false;
    private int squatCount = 0;
    private String feedback = "Kameraya Geçin ve Hazırlanın";

    public void processPose(Pose pose) {
        if (pose == null) return;

        // Try to get left leg landmarks
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);

        // Try to get right leg landmarks
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

        double angle = 0.0;

        // We use the side that has more visible/confident landmarks
        if (leftHip != null && leftKnee != null && leftAnkle != null &&
            leftHip.getInFrameLikelihood() > 0.5f) {
            angle = PoseUtils.getAngle(leftHip, leftKnee, leftAnkle);
        } else if (rightHip != null && rightKnee != null && rightAnkle != null &&
                   rightHip.getInFrameLikelihood() > 0.5f) {
            angle = PoseUtils.getAngle(rightHip, rightKnee, rightAnkle);
        } else {
            feedback = "Vücudunuz tam görünmüyor";
            return;
        }

        // Logic for counting squats
        if (angle > SQUAT_UP_THRESHOLD) {
            if (isDown) {
                // User was down and now is up -> count a rep!
                squatCount++;
                isDown = false;
                feedback = "Harika! Bir tane daha.";
            } else {
                feedback = "Hazır! Çömelmeye başla.";
            }
        } else if (angle < SQUAT_DOWN_THRESHOLD) {
            isDown = true;
            feedback = "Pozisyon iyi, şimdi kalk!";
        } else {
            if (!isDown) {
                feedback = "Daha da eğil...";
            } else {
                feedback = "Kalkmaya devam et...";
            }
        }
    }

    public int getSquatCount() {
        return squatCount;
    }

    public String getFeedback() {
        return feedback;
    }
}
