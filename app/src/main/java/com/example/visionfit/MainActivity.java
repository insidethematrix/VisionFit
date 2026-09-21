package com.example.visionfit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.visionfit.analyzer.PoseAnalyzer;
import com.example.visionfit.overlay.GraphicOverlay;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main entry point of the application.
 * This activity handles camera permissions and initializes the CameraX preview.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VisionFitApp";
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private GraphicOverlay graphicOverlay; // YENİ: Overlay referansı

    private TextView tvSquatCount;
    private TextView tvFeedback;

    // YENİ: Görüntü analizi arka planda yapılmalı, ana thread donmasın diye.
    private ExecutorService cameraExecutor;

    /**
     * Permission launcher to handle the result of the camera permission request.
     * If granted, starts the camera; otherwise, notifies the user.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Camera permission required!", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the PreviewView from the layout (used for displaying the camera feed)
        previewView = findViewById(R.id.previewView);

        // YENİ: XML'deki çizim alanını koda bağlıyoruz.
        graphicOverlay = findViewById(R.id.graphicOverlay);

        tvSquatCount = findViewById(R.id.tvSquatCount);
        tvFeedback = findViewById(R.id.tvFeedback);

        // YENİ: Arka plan işçisini (Thread) işe alıyoruz.
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Check if camera permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            // Request camera permission if not already granted
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Initializes the CameraX process provider.
     */
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Add a listener to the future to handle camera provider initialization
        cameraProviderFuture.addListener(() -> {
            try {
                // The camera provider is now ready
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Failed to start camera.", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        // Select the back camera as the default lens
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Connect the Preview use case to the PreviewView
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        PoseAnalyzer analyzer = new PoseAnalyzer(graphicOverlay, (count, feedback) -> {
            // Update UI on the main thread
            runOnUiThread(() -> {
                tvSquatCount.setText("Squat: " + count);
                tvFeedback.setText(feedback);
            });
        });

        imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

        try {
            // Unbind any previous use cases before rebinding
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}