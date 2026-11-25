# Yomo Style Launcher (starter project)

A lightweight native Android launcher starter with:
- HOME/LAUNCHER intent filters so Android can offer it as a default home app
- Six built-in gradient wallpapers
- Gallery/document picker for a custom image wallpaper
- A basic app drawer that launches installed apps

## Build an APK

This workspace did not have the Android SDK or Gradle installed, so an APK could not be compiled here. To build it:

1. Install Android Studio (with Android SDK Platform 35 and a compatible JDK).
2. Open this `YomoStyleLauncher` folder in Android Studio.
3. Allow Gradle sync to finish.
4. Select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. Android Studio will show the generated APK location (normally `app/build/outputs/apk/debug/app-debug.apk`).
6. Transfer the APK to your Android phone, install it, then choose **Yomo Style Launcher** when Android asks for the Home app. You can also change the default under Settings → Apps → Default apps → Home app.

## Notes

- Package name: `com.example.yomostylelauncher`
- Minimum Android version: Android 6.0 (API 23)
- The home screen/app drawer is a simple first version. It does not yet include icon packs, gesture customization, widget hosting, or live clock updates.
