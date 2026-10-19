# TaqvimCod R8 rules (release hardening)
# Obfuscate + shrink everything possible; keep only what the platform needs.

# Readable crash traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Manifest-referenced components are kept automatically by AGP, but be explicit
# for the widget provider (instantiated by the launcher via AppWidgetManager).
-keep class com.taqvimcod.calendar.WidgetProvider { *; }

# Keep enums/values used by reflection-free framework callbacks
-dontwarn javax.annotation.**
