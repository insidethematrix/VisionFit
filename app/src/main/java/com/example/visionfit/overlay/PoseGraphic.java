package com.example.visionfit.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;

public class PoseGraphic extends GraphicOverlay.Graphic {
    private final Pose pose;
    private final Paint dotPaint;
    private final Paint linePaint;

    public PoseGraphic(GraphicOverlay overlay, Pose pose) {
        super(overlay);
        this.pose=pose;

        dotPaint = new Paint();
        dotPaint.setColor(Color.GREEN);
        dotPaint.setStrokeWidth(10.0f);


        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(5.0f);
    }
    @Override
    public void draw(Canvas canvas){
        List<PoseLandmark>landmarks=pose.getAllPoseLandmarks();

        for(PoseLandmark landmark:landmarks){
            // FILTRATION: Skip landmarks that are not clearly visible.
            // This prevents "ghost" points from appearing on the screen due to noise.
            if(landmark==null || landmark.getInFrameLikelihood() < 0.5f){
                continue;
            }
            float x =translateX(landmark.getPosition().x);
            float y =translateY(landmark.getPosition().y);

            canvas.drawCircle(x, y, 10.0f, dotPaint);
        }


        // --- TORSO ---
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER), pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER));
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_HIP), pose.getPoseLandmark(PoseLandmark.RIGHT_HIP));
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER), pose.getPoseLandmark(PoseLandmark.LEFT_HIP));
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER), pose.getPoseLandmark(PoseLandmark.RIGHT_HIP));

        // --- LEFT ARM ---
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER), pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW));
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW), pose.getPoseLandmark(PoseLandmark.LEFT_WRIST));

        // --- RIGHT ARM ---
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER), pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW));
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW), pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST));

        // --- LEFT LEG ---
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_HIP), pose.getPoseLandmark(PoseLandmark.LEFT_KNEE));
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_KNEE), pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE));

        // --- RIGHT LEG ---
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.RIGHT_HIP), pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE));
        drawLine(canvas, pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE), pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE));

    }

    private void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark) {
        // 1. SAFETY CHECK: Ensure both landmarks exist.
        // ML Kit might not detect a body part if it's out of frame.
        if(startLandmark==null || endLandmark== null){
            return;
        }
        // This prevents drawing shaky or incorrect lines connected to weak points.
        if(startLandmark.getInFrameLikelihood() < 0.5f || endLandmark.getInFrameLikelihood() < 0.5f){
            return;
        }
        float x1 =translateX(startLandmark.getPosition().x);
        float y1 =translateY(startLandmark.getPosition().y);
        float x2 =translateX(endLandmark.getPosition().x);
        float y2 =translateY(endLandmark.getPosition().y);
        canvas.drawLine(x1, y1, x2, y2, linePaint);
    }

}
