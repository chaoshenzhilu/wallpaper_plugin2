import 'dart:async';

import 'package:flutter/services.dart';

class WallpaperPlugin {
  static const MethodChannel _channel = const MethodChannel('wallpaper_plugin');

  static Future<String> setHomeScreenWallpaper(String path) async {
    final Map<String, dynamic> params = <String, dynamic>{'path': path};
    final String isSuccess = await _channel.invokeMethod('HomeScreen', params);
    return isSuccess;
  }

  static Future<bool> setLockScreenWallpaper(String path) async {
    final Map<String, dynamic> params = <String, dynamic>{'path': path};
    final bool isSuccess = await _channel.invokeMethod('LockScreen', params);
    return isSuccess;
  }

  static Future<bool> setBothWallpaper(String path) async {
    final Map<String, dynamic> params = <String, dynamic>{'path': path};
    final bool isSuccess = await _channel.invokeMethod('Both', params);
    return isSuccess;
  }

  static Future<bool> setSystemWallpaper(String path) async {
    final Map<String, dynamic> params = <String, dynamic>{'path': path};
    final bool isSuccess = await _channel.invokeMethod('System', params);
    return isSuccess;
  }
}
