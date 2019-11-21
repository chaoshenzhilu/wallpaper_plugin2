package com.szh.wallpaper_plugin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;

public class WallpaperTool {
    private static String TAG = "WallpaperTool";
    public static int SET_WALLPAPER = 1234;

    static boolean setWallpaperByIntent(Activity activity, String path) {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mimeType", "image/*");
        try {
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), path, null, null));
            intent.setData(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        activity.startActivityForResult(intent, SET_WALLPAPER);
        return true;
    }
}