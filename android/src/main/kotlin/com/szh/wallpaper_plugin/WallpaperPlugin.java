package com.szh.wallpaper_plugin;

import android.app.Activity;
import android.content.Intent;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class WallpaperPlugin implements MethodChannel.MethodCallHandler {
    private Activity activity;

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
                if (requestCode == WallpaperTool.SET_WALLPAPER) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "getSetWallpaper":
                String path = call.argument("path").toString();
                result.success(WallpaperTool.setWallpaperByIntent(activity, path));
            default:
                result.notImplemented();
                break;
        }
    }
}