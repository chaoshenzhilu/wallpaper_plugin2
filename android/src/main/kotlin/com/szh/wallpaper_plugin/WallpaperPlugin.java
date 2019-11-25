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
import android.widget.Toast;

import java.io.File;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class WallpaperPlugin implements MethodChannel.MethodCallHandler {
    private final static int REQUEST_CODE_SET_WALLPAPER = 0x001;
    private final static int REQUEST_CODE_SELECT_SYSTEM_WALLPAPER = 0x002;
    private static String Tag = "WallpaperPlugin";
    private Activity activity;
    private int id;
    private static String res = "";
    MethodChannel.Result result;

    private WallpaperPlugin(Activity activity) {
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
            public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
//                if (requestCode == REQUEST_CODE_SET_WALLPAPER) {
//                    if (resultCode == Activity.RESULT_OK) {
//                        // TODO: 2017/3/13 设置动态壁纸成功
//                        Toast.makeText(registrar.activity(), "", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // TODO: 2017/3/13 取消设置动态壁纸
//                        Toast.makeText(registrar.activity(), "", Toast.LENGTH_SHORT).show();
//                    }
//                } else if (requestCode == REQUEST_CODE_SELECT_SYSTEM_WALLPAPER) {
//                    if (resultCode == Activity.RESULT_OK) {
//                        Toast.makeText(registrar.activity(), "", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(registrar.activity(), "", Toast.LENGTH_SHORT).show();
//                    }
//                }
                return false;
            }
        });
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        this.result = result;
        String path = call.argument("path").toString();
        switch (call.method) {
            case "System":
                setWallpaper(path, REQUEST_CODE_SELECT_SYSTEM_WALLPAPER);
                break;
            case "Video":
                LiveWallpaperService.startWallPaper(activity, path);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @SuppressLint("MissingPermission")
    private void setWallpaper(String path, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                File file = new File(path);
                Uri contentURI = getImageContentUri(activity, file);
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                Intent intent = new Intent(wallpaperManager.getCropAndSetWallpaperIntent(contentURI));
                String mime = "image/*";
                intent.setDataAndType(contentURI, mime);
                try {
                    activity.startActivityForResult(intent, requestCode);
                } catch (ActivityNotFoundException e) {
                }
            }
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            boolean isSuccess = WallpaperUtil.onSetWallpaperForBitmap(activity, bitmap);
            if (isSuccess) {
                Toast.makeText(activity, "设置壁纸成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "设置壁纸失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static Uri getImageContentUri(Context context, File imageFile) {
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
            cursor.close();
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