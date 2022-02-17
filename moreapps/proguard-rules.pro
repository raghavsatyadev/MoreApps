-keep public class com.github.raghavsatyadev.moreapps.** { *;}

-dontwarn rx.**
-dontwarn javax.annotation.**
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.Nullable

#retrofit models

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
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#RXJava
-dontwarn io.reactivex.**
