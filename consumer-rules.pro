# Consumer ProGuard rules for library users

# Keep all public APIs
-keep public class com.nexustech.smartbrowser.** { public *; }

# Keep JavaScript interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
