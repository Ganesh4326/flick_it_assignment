package com.example.flickit.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flickit.R;
import com.example.flickit.utils.ColorAnalysisUtils;
import com.example.flickit.utils.ExtractFramesTask;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VideoPlaybackActivity extends AppCompatActivity implements ExtractFramesTask.OnFramesExtractedListener {

    private static final String TAG = "VideoPlaybackActivity";
    private List<Bitmap> frames;
    private TextView colorOverlayTextView;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playback);

        videoView = findViewById(R.id.videoView);
        colorOverlayTextView = findViewById(R.id.colorOverlayTextView);

        Uri videoUri = getIntent().getData();
        if (videoUri != null) {
            videoView.setVideoURI(videoUri);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            new ExtractFramesTask(this, this).execute(videoUri);
        }
    }


    @Override
    public void onFramesExtracted(List<Bitmap> extractedFrames) {
        if (extractedFrames != null && !extractedFrames.isEmpty()) {
            frames = extractedFrames;
            analyzeColorsAsync();
        } else {
            Log.e(TAG, "Failed to extract frames from video");
            Toast.makeText(this, "Failed to extract frames from video", Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeColorsAsync() {
        new Thread(() -> {
            List<Integer> colors = ColorAnalysisUtils.analyzeFrames(frames);
            runOnUiThread(() -> {
                if (colors != null && !colors.isEmpty()) {
                    Map<Integer, Integer> colorCountMap = countColors(colors);
                    displayProminentColors(colorCountMap);
                } else {
                    Log.e(TAG, "Failed to analyze colors");
                    Toast.makeText(this, "Failed to analyze colors", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private Map<Integer, Integer> countColors(List<Integer> colors) {
        Map<Integer, Integer> colorCountMap = new TreeMap<>();
        for (int color : colors) {
            colorCountMap.put(color, colorCountMap.getOrDefault(color, 0) + 1);
        }
        return colorCountMap;
    }

    private void displayProminentColors(Map<Integer, Integer> colorCountMap) {
        StringBuilder overlayTextBuilder = new StringBuilder();
        overlayTextBuilder.append("Prominent Colors:\n");
        int totalCount = frames.size();
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : colorCountMap.entrySet()) {
            int color = entry.getKey();
            int frequency = entry.getValue();
            double percentage = (double) frequency / totalCount * 100;
            overlayTextBuilder.append(String.format("#%06X : %.2f%%\n", color, percentage));
            count++;
            if (count >= 3) {
                break;
            }
        }
        colorOverlayTextView.setText(overlayTextBuilder.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }

}
