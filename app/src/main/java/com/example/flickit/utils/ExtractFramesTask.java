package com.example.flickit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractFramesTask extends AsyncTask<Uri, Void, List<Bitmap>> {
    private Context context;

    private static final int FRAME_INTERVAL_MS = 1000;
    private OnFramesExtractedListener listener;

    public ExtractFramesTask(Context context, OnFramesExtractedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Bitmap> doInBackground(Uri... uris) {
        Uri videoUri = uris[0];
        List<Bitmap> frames = new ArrayList<>();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, videoUri);

        long durationMs = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        for (long timeMs = 0; timeMs < durationMs; timeMs += FRAME_INTERVAL_MS) {
            Bitmap frame = retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (frame != null) {
                frames.add(frame);
            }
        }

        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return frames;
    }

    @Override
    protected void onPostExecute(List<Bitmap> frames) {
        if (listener != null) {
            listener.onFramesExtracted(frames);
        }
    }

    public interface OnFramesExtractedListener {
        void onFramesExtracted(List<Bitmap> frames);
    }
}
