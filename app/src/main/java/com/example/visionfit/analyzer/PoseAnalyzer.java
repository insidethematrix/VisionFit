package com.example.visionfit.analyzer;

import android.media.Image;

import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.example.visionfit.overlay.GraphicOverlay;
import com.example.visionfit.overlay.PoseGraphic;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

public class PoseAnalyzer implements ImageAnalysis.Analyzer {
    // The core engine of ML Kit that will process our frames
    private final PoseDetector poseDetector;
    // The transparent layer where we will draw the skeleton results
    private final GraphicOverlay overlay;

    public PoseAnalyzer(GraphicOverlay overlay) {
        this.overlay = overlay;

        // Configuring the detector for optimal performance in a video stream
        PoseDetectorOptions options = new PoseDetectorOptions.Builder()
                // STREAM_MODE tells ML Kit to treat frames as a sequence (faster)
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build();
        // Initialize the PoseDetector with the specified options
        this.poseDetector = PoseDetection.getClient(options);
    }

    @Override
    // This annotation is required because 'getImage()' is still in beta/experimental
    @ExperimentalGetImage
    public void analyze(ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            // CRITICAL: Handle Image Rotation.
            // The camera sensor usually reads data in landscape (90 degrees off from portrait UI).
            // We must pass this rotation info to ML Kit so it "sees" the person upright.
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();

            boolean isPortrait = rotationDegrees == 90 || rotationDegrees == 270;

            if(isPortrait){
                overlay.setCameraInfo(mediaImage.getHeight() , mediaImage.getWidth(),false);
            }
            else{
                overlay.setCameraInfo(mediaImage.getWidth() , mediaImage.getHeight() , false);
            }

            InputImage image = InputImage.fromMediaImage(mediaImage, rotationDegrees);

            poseDetector.process(image).addOnSuccessListener(pose -> {
                overlay.clear();
                overlay.add(new PoseGraphic(overlay, pose));
                overlay.postInvalidate();
            }).addOnFailureListener(e -> {
                e.printStackTrace();
            }).addOnCompleteListener(task -> {
                // MEMORY MANAGEMENT (CRITICAL):
                // We MUST close the imageProxy when processing is done.
                // If we don't, CameraX will think we are still using the frame buffer
                // and will STOP sending new frames (App will freeze).
                imageProxy.close();
            });

        }
        else{
            // Even if the image is null, we must close the proxy to release the buffer.
            imageProxy.close();
        }

        
    }
}
