import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:wallpaper_plugin/wallpaper_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: MaterialButton(
            onPressed: () {
              WallpaperPlugin.setWallpaper(
                  'Android/data/com.wallpaper.flutter_wallpaper/files/https:goss.veer.comcreativevcgveer800waterveer-147011245.jpg');
            },
            child: Text('设置壁纸'),
          ),
        ),
      ),
    );
  }
}
