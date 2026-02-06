package com.example.yum_yum.presentation.utils;

import android.util.Log;

public class YouTubeUtils {

    private static final String TAG = "YouTubeUtils";
    public static String extractVideoId(String url) {
        if (url == null || url.isEmpty()) {
            Log.w(TAG, "URL is null or empty");
            return null;
        }
        String videoId = null;
        try {
            url = url.trim();
            Log.d(TAG, "Processing URL: " + url);
            if (url.contains("watch?v=")) {
                videoId = extractBetween(url, "watch?v=", "&", "#");
                Log.d(TAG, "Pattern 1 (watch?v=) - Extracted: " + videoId);
            }
            else if (url.contains("youtu.be/")) {
                videoId = extractBetween(url, "youtu.be/", "?", "#");
                Log.d(TAG, "Pattern 2 (youtu.be/) - Extracted: " + videoId);
            }
            else if (url.contains("/embed/")) {
                videoId = extractBetween(url, "/embed/", "?", "#");
                Log.d(TAG, "Pattern 3 (/embed/) - Extracted: " + videoId);
            }
            else if (url.contains("/v/")) {
                videoId = extractBetween(url, "/v/", "?", "#");
                Log.d(TAG, "Pattern 4 (/v/) - Extracted: " + videoId);
            }
            if (videoId != null) {
                videoId = videoId.replaceAll("[^a-zA-Z0-9_-]", "");
                if (videoId.length() != 11) {
                    Log.w(TAG, "Invalid video ID length: " + videoId.length() + " (expected 11)");
                    Log.w(TAG, "Video ID: " + videoId);
                }
                Log.d(TAG, "Final Video ID: " + videoId);
            } else {
                Log.w(TAG, "Could not extract video ID from URL");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error extracting video ID", e);
            return null;
        }

        return videoId;
    }

    private static String extractBetween(String text, String start, String end1, String end2) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1) {
            return null;
        }
        startIndex += start.length();
        int endIndex1 = text.indexOf(end1, startIndex);
        int endIndex2 = text.indexOf(end2, startIndex);

        int endIndex;
        if (endIndex1 == -1 && endIndex2 == -1) {
            endIndex = text.length();
        } else if (endIndex1 == -1) {
            endIndex = endIndex2;
        } else if (endIndex2 == -1) {
            endIndex = endIndex1;
        } else {
            endIndex = Math.min(endIndex1, endIndex2);
        }

        return text.substring(startIndex, endIndex);
    }

    public static boolean isYouTubeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        return url.contains("youtube.com") || url.contains("youtu.be");
    }
}