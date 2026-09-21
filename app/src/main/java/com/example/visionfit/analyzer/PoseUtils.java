package com.example.visionfit.analyzer;

import com.google.mlkit.vision.pose.PoseLandmark;

public class PoseUtils {

    /**
     * Calculates the angle between three landmarks.
     * The angle is calculated at the second landmark (p2).
     *
     * @param firstLandmark  The first point (e.g., Hip)
     * @param middleLandmark The middle point where the angle is measured (e.g., Knee)
     * @param lastLandmark   The last point (e.g., Ankle)
     * @return The angle in degrees (0-180)
     */
    public static double getAngle(PoseLandmark firstLandmark, PoseLandmark middleLandmark, PoseLandmark lastLandmark) {
        if (firstLandmark == null || middleLandmark == null || lastLandmark == null) {
            return 0.0;
        }

        double angle = Math.toDegrees(
                Math.atan2(lastLandmark.getPosition().y - middleLandmark.getPosition().y,
                           lastLandmark.getPosition().x - middleLandmark.getPosition().x)
                - Math.atan2(firstLandmark.getPosition().y - middleLandmark.getPosition().y,
                             firstLandmark.getPosition().x - middleLandmark.getPosition().x)
        );

        angle = Math.abs(angle);

        if (angle > 180) {
            angle = 360.0 - angle;
        }

        return angle;
    }
}
