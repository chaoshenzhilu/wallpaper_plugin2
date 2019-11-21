import 'dart:async';

import 'package:flutter/services.dart';

class WallpaperPlugin {
  static const MethodChannel _channel = const MethodChannel('wallpaper_plugin');

  static Future<bool> setWallpaper(String path) async {
    final Map<String, dynamic> params = <String, dynamic>{'path': path};
    final bool isSuccess =
        await _channel.invokeMethod('getSetWallpaper', params);
    return isSuccess;
  }
}
