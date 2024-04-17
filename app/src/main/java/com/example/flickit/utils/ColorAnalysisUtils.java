package com.example.flickit.utils;

import android.graphics.Bitmap;

import androidx.palette.graphics.Palette;

import java.util.ArrayList;
import java.util.List;

public class ColorAnalysisUtils {

    public static List<Integer> analyzeFrames(List<Bitmap> frames) {
        List<Integer> colors = new ArrayList<>();

        for (Bitmap frame : frames) {
            int color = sampleAndAnalyzeColors(frame);
            colors.add(color);
        }

        return colors;
    }

    private static int sampleAndAnalyzeColors(Bitmap frame) {
        Palette palette = Palette.from(frame).generate();
        return palette.getDominantColor(0xFF000000);
    }
}
