# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn javax.annotation.**

-keepattributes Signature,*Annotation*

# MuPDF Android SDK for viewing pdf files
-keep class com.artifex.mupdf.** { *; }

#Jsoup
-keep public class org.jsoup.** { public *; }
-keeppackagenames org.jsoup.nodes

#Apache POI
-keep class org.apache.poi.** { *; }

# OkHTTTP
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepclasseswithmembers class okhttp3.OkHttpClient
-keepclasseswithmembers class okhttp3.Request
-keepclasseswithmembers class okhttp3.RequestBody
-keepclasseswithmembers class okhttp3.Response
-keepclasseswithmembers class okhttp3.ResponseBody