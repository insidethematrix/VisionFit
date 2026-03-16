package com.example.visionfit.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * A transparent layer that draws graphics over the camera preview.
 * This class handles coordinate scaling to ensure that graphics
 * (like skeletal points) align correctly with the camera image.
 */
public class GraphicOverlay extends View{
    // List of all graphics (pose points, lines, text) to be drawn on the screen.
    private final List<Graphic> graphics = new ArrayList<>();
    // Lock object to provide thread safety between the
    // Analysis thread (ML Kit) and the UI thread (Drawing).
    private final Object lock = new Object();

    // Scaling factors used to transform camera coordinates to screen coordinates.
    private float scaleFactor = 1.0f;
    private float postScaleWidthOffset = 0;
    private float postScaleHeightOffset = 0;
    private boolean isImageFlipped = false;
    private int previewWidth;
    private int previewHeight;
    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Removes all graphics from the overlay.
     * Called at the start of each new frame to clear the previous drawings.
     */
    public void clear(){
        synchronized (lock){
            graphics.clear();
        }
        postInvalidate();
    }

    /**
     * Adds a new graphic object to the overlay.
     */
    public void add(Graphic graphic){
        synchronized (lock){
            graphics.add(graphic);
        }
        postInvalidate();
    }

    /**
     * Updates the camera preview dimensions.
     * This is crucial for calculating the correct scaling factors.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, boolean isFlipped){
        synchronized (lock){
            this.previewWidth=previewWidth;
            this.previewHeight=previewHeight;
            this.isImageFlipped=isFlipped;
        }
        postInvalidate();
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        synchronized (lock){
            // Calculate scale factors: Screen Dimension / Camera Dimension
            if (previewWidth!=0 && previewHeight!=0){
                float viewAspectRatio = (float) getWidth() / getHeight();
                float imageAspectratio = (float) previewWidth / previewHeight;

                if(viewAspectRatio > imageAspectratio){
                    scaleFactor = (float) getWidth() / previewWidth;
                }
                else{
                    scaleFactor = (float) getHeight() / previewHeight;
                }
                float scaledWidth = previewWidth * scaleFactor;
                float scaleHeight = previewHeight * scaleFactor;

                postScaleWidthOffset = (getWidth() - scaledWidth) / 2;
                postScaleHeightOffset = (getHeight() - scaleHeight) / 2;
            }
            // Iterate through the list and draw each graphic object on the canvas.
            for (Graphic graphic : graphics){
                graphic.draw(canvas);
            }
        }

    }

    // Helper methods to translate camera coordinates to screen coordinates.
    public float translateX(float x) {
        if(isImageFlipped){
            return  getWidth() - (x * scaleFactor + postScaleWidthOffset);
        }
        else{
            return x * scaleFactor + postScaleWidthOffset;
        }
    }

    public float translateY(float y) {
        return y * scaleFactor + postScaleHeightOffset;
    }

    /**
     * Abstract base class for all items to be drawn on GraphicOverlay.
     */
    public static abstract class Graphic{
        private GraphicOverlay overlay;
        public Graphic(GraphicOverlay overlay){
            this.overlay=overlay;
        }

        /**
         * Subclasses must implement this method to perform actual drawing on the canvas.
         */
        public abstract void draw(Canvas canvas);

        // Provides access to the parent overlay's coordinate transformation methods.
        public float translateX(float x) {
            return overlay.translateX(x);
        }

        public float translateY(float y) {
            return overlay.translateY(y);
        }
    }

}