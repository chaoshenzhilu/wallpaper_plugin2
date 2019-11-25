package com.szh.wallpaper_plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;


/**
 *
 */
public class WallpaperUtil {

    /**
     * 跳转到系统设置壁纸界面
     *
     * @param context
     * @param paramActivity
     */
    public static void setLiveWallpaper(Context context, Activity paramActivity, int requestCode) {
        try {
            Intent localIntent = new Intent();
            localIntent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);//android.service.wallpaper.CHANGE_LIVE_WALLPAPER
            //android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT
            localIntent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT
                    , new ComponentName(context.getApplicationContext().getPackageName()
                            , LiveWallpaperService.class.getCanonicalName()));
            paramActivity.startActivityForResult(localIntent, requestCode);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 判断是否是使用我们的壁纸
     *
     * @param paramContext
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean wallpaperIsUsed(Context paramContext) {
        WallpaperInfo localWallpaperInfo = null;
        localWallpaperInfo = WallpaperManager.getInstance(paramContext).getWallpaperInfo();
        return ((localWallpaperInfo != null) && (localWallpaperInfo.getPackageName().equals(paramContext.getPackageName())) &&
                (localWallpaperInfo.getServiceName().equals(LiveWallpaperService.class.getCanonicalName())));
    }

    @SuppressLint("NewApi")
    public static Bitmap getDefaultWallpaper(Context paramContext) {
        Bitmap localBitmap;
        if (isLivingWallpaper(paramContext))
            localBitmap = null;
        do {
            localBitmap = ((BitmapDrawable) WallpaperManager.getInstance(paramContext).getDrawable()).getBitmap();
            return localBitmap;
        }
        while (localBitmap != null);
    }

    @SuppressLint("NewApi")
    public static boolean isLivingWallpaper(Context paramContext) {
        return (WallpaperManager.getInstance(paramContext).getWallpaperInfo() != null);
    }
}
