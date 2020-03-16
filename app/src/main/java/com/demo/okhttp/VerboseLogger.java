package com.demo.okhttp;

import android.util.Log;

/**
 * Created by xin on 20/12/2017.
 * Logcat print verbose info
 */

public class VerboseLogger {

    public static void printE(String tag, String info) {
        info = info.trim();
        int index = 0;
        int maxLength = 4000;
        String sub;

        while (index < info.length()) {
            if (info.length() <= index + maxLength) {
                sub = info.substring(index);
            } else {
                sub = info.substring(index, maxLength + index);
            }
            index += maxLength;
            Log.e(tag, sub.trim());
        }
    }

    public static void printV(String tag, String info) {
        info = info.trim();
        int index = 0;
        int maxLength = 4000;
        String sub;

        while (index < info.length()) {
            if (info.length() <= index + maxLength) {
                sub = info.substring(index);
            } else {
                sub = info.substring(index, maxLength + index);
            }
            index += maxLength;
            Log.v(tag, sub.trim());
        }
    }
}
