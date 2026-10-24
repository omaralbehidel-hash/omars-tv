#!/bin/bash
mkdir -p shots
APK=app/build/outputs/apk/debug/app-debug.apk
adb install -r "$APK" || exit 0
adb logcat -c
adb shell am start -n sa.omars.tv/.MainActivity
sleep 15
shot(){ adb exec-out screencap -p > "shots/$1.png"; }
key(){ adb shell input keyevent "$1"; sleep 1.2; }
shot 01_start
adb shell uiautomator dump /sdcard/ui1.xml >/dev/null 2>&1; adb pull /sdcard/ui1.xml shots/ui1.xml >/dev/null 2>&1
key KEYCODE_DPAD_CENTER
sleep 25
shot 02_playing
key KEYCODE_DPAD_DOWN
key KEYCODE_DPAD_DOWN
shot 03_nav
key KEYCODE_MENU
sleep 2
shot 04_settings
adb shell uiautomator dump /sdcard/ui4.xml >/dev/null 2>&1; adb pull /sdcard/ui4.xml shots/ui4.xml >/dev/null 2>&1
key KEYCODE_DPAD_DOWN
key KEYCODE_DPAD_DOWN
shot 05_settings_nav
key KEYCODE_BACK
sleep 1
key KEYCODE_DPAD_UP
key KEYCODE_DPAD_UP
key KEYCODE_DPAD_UP
key KEYCODE_DPAD_UP
shot 06_after
adb logcat -d -s OMARSDBG > shots/dbg.txt
adb logcat -d -t 3000 > shots/logcat.txt
adb logcat -d -b crash > shots/crash.txt
adb shell dumpsys activity activities | grep -i "mResumedActivity" > shots/resumed.txt
