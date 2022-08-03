-keep public class io.github.raghavsatyadev.moreapps.** { *;}

-dontwarn rx.**
-dontwarn javax.annotation.**
-dontwarn okio.**
-dontwarn okhttp3.**

#models

-keepattributes InnerClasses
-keepattributes Deprecated
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
#Retrofit End

-keep public class * extends java.lang.Exception

#Glide
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.**

#Glide Module
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#RXJava
-dontwarn io.reactivex.**

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-dontwarn java.lang.invoke.StringConcatFactory