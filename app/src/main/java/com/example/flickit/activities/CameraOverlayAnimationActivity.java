package com.example.flickit.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.flickit.R;
import com.example.flickit.utils.CameraHelper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.opencv.android.OpenCVLoader;

public class CameraOverlayAnimationActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private TextureView textureView;
    private Button button;
    private View colorCircle;
    private Executor executor = Executors.newSingleThreadExecutor();

    // Variables for gesture detection
    private boolean isBlowing = false;
    private static final int MIN_PIXEL_INTENSITY_CHANGE = 50; // Minimum pixel intensity change to detect blowing
    private static final int MIN_AREA_CHANGE = 500; // Minimum area change to detect blowing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_overlay_animation);

        textureView = findViewById(R.id.textureView);
        button = findViewById(R.id.button);
        colorCircle = findViewById(R.id.colorCircle);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CameraOverlayAnimationActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(CameraOverlayAnimationActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            }
        });

        // Set onTouchListener to detect touch events
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                // Check if touch event is within the bounds of the red circle
                if (isInsideCircle(x, y)) {
                    // Hide the red circle
                    colorCircle.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

        // Start background task for gesture detection
        startGestureDetection();
    }

    private void openCamera() {
        CameraHelper cameraHelper = new CameraHelper(this, textureView);
        cameraHelper.openCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to check if touch event is inside the red circle
    private boolean isInsideCircle(float x, float y) {
        float circleX = colorCircle.getX() + colorCircle.getWidth() / 2;
        float circleY = colorCircle.getY() + colorCircle.getHeight() / 2;
        float radius = colorCircle.getWidth() / 2;
        double distance = Math.sqrt(Math.pow(x - circleX, 2) + Math.pow(y - circleY, 2));
        return distance <= radius;
    }

    // Background task for gesture detection
    private void startGestureDetection() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Capture camera frame
                    Bitmap frame = captureFrame();

                    // Preprocess frame
                    Mat processedFrame = preprocessFrame(frame);

                    // Detect blowing gesture
                    boolean isBlowingGesture = detectBlowingGesture(processedFrame);

                    // Trigger action if blowing gesture detected
                    if (isBlowingGesture) {
                        hideCircle();
                    }
                }
            }
        });
    }

    // Method to capture camera frame (placeholder)
    // Placeholder method to capture camera frame
    private Bitmap captureFrame() {
        // Capture camera frame using CameraX or another camera library
        // Convert the captured frame to a Bitmap
        Bitmap frame = null; // Placeholder
        return frame;
    }

    // Placeholder method to preprocess camera frame
    private Mat preprocessFrame(Bitmap frame) {
        // Convert the Bitmap to a Mat (OpenCV Mat)
        Mat matFrame = new Mat(); // Placeholder
        Utils.bitmapToMat(frame, matFrame);

        // Convert the Mat to grayscale
        Imgproc.cvtColor(matFrame, matFrame, Imgproc.COLOR_RGB2GRAY);

        // Apply additional preprocessing steps as needed
        // For example, you can perform Gaussian blur, thresholding, or edge detection

        return matFrame;
    }

    // Placeholder method to detect blowing gesture
    private boolean detectBlowingGesture(Mat frame) {
        // Placeholder implementation for blowing gesture detection
        // You need to implement actual blowing gesture detection logic here
        // For example, you can look for sudden changes in pixel intensity or motion patterns
        // You can use basic image processing techniques like frame differencing, thresholding, or edge detection
        // Return true if blowing gesture is detected, false otherwise
        return false;
    }


    // Method to hide the red circle
    private void hideCircle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                colorCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
}
