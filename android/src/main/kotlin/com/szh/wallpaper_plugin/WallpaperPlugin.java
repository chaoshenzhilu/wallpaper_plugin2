package com.szh.wallpaper_plugin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class WallpaperPlugin implements MethodChannel.MethodCallHandler {
    private static String Tag = "WallpaperPlugin";
    private Activity activity;
    private int id;
    private static String res = "";

    public WallpaperPlugin(Activity activity) {
        this.activity = activity;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "wallpaper_plugin");
        channel.setMethodCallHandler(new WallpaperPlugin(registrar.activity()));
        registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
            @Override
            public boolean onActivityResult(int requestCode, int responseCode, Intent intent) {
                Log.e(Tag, "resultcode=" + responseCode + "requestcode=" + requestCode);
                if (responseCode == Activity.RESULT_OK) {
                    res = "System Screen Set Successfully";
                } else if (responseCode ==  Activity.RESULT_CANCELED) {
                    res = "setting Wallpaper Cancelled";
                } else {
                    res = "Something Went Wrong";
                }
                return false;
            }
        });
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "HomeScreen":
                result.success(setWallpaper(1, (String) call.arguments));
                break;
            case "LockScreen":
                result.success(setWallpaper(2, (String) call.arguments));
                break;
            case "Both":
                result.success(setWallpaper(3, (String) call.arguments));
                break;
            case "System":
                result.success(setWallpaper(4,(String) call.arguments));
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @SuppressLint("MissingPermission")
    private String setWallpaper(int i, String path) {
        id = i;
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);

        File file = new File(activity.getExternalFilesDir(null), path);
        //File file = new File(Activity.ge);
        //Activity.getDir("flutter", 0).getPath()
        // set bitmap to wallpaper
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (id == 1) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    res = "Home Screen Set Successfully";
                } else {
                    res = "To Set Home Screen Requires Api Level 24";
                }


            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (id == 2) try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                res = "Lock Screen Set Successfully";
            } else {
                res = "To Set Lock Screen Requires Api Level 24";
            }


        } catch (IOException e) {
            res = e.toString();
            e.printStackTrace();
        }
        else if (id == 3) {
            try {
                wallpaperManager.setBitmap(bitmap);
                res = "Home And Lock Screen Set Successfully";
            } catch (IOException e) {
                res = e.toString();
                e.printStackTrace();
            }

        } else if (id == 4) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                        activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    Uri uri = Uri.fromFile(file);
                    Uri contentURI = getImageContentUri(activity, file);

                    Intent intent = new Intent(wallpaperManager.getCropAndSetWallpaperIntent(contentURI));
                    String mime = "image/*";
                    if (intent != null) {
                        intent.setDataAndType(contentURI, mime);
                    }
                    try {
                        activity.startActivityForResult(intent, 2);
                    } catch (ActivityNotFoundException e) {
                        //handle error
                        res = "Error To Set Wallpaer";
                    }
                }
            }
        }

        return res;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Log.d("Tag", filePath);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}