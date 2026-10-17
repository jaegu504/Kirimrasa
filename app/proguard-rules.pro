# ==============================================================================
# WhatsApp Bulk Sender - Production Proguard / R8 Configuration
# ==============================================================================

# ------------------------------------------------------------------------------
# General Keep Rules & Attributes
# ------------------------------------------------------------------------------
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ------------------------------------------------------------------------------
# Kotlinx Serialization
# ------------------------------------------------------------------------------
-dontnote kotlinx.serialization.SerializationKt

-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclassmembers class * {
    @kotlinx.serialization.Serializable class *;
    *** Companion;
}

-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

-keep class * implements kotlinx.serialization.internal.GeneratedSerializer {
    <init>(...);
}

-keep class kotlinx.serialization.** { *; }

# Keep domain / network DTOs
-keep class com.wabulk.sender.data.remote.dto.** { *; }
-keep class com.wabulk.sender.engine.baileys.** { *; }

# ------------------------------------------------------------------------------
# AndroidX Room SQLite
# ------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Entity class * { *; }
-keep class * extends androidx.room.migration.Migration { *; }
-keepclassmembers class * {
    @androidx.room.Database *;
    @androidx.room.Dao *;
    @androidx.room.Entity *;
}
-dontwarn androidx.room.paging.**

# Keep local database entities and DAOs
-keep class com.wabulk.sender.data.local.db.** { *; }

# ------------------------------------------------------------------------------
# OkHttp, Okio & WebSocket
# ------------------------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keepclassmembers class * extends okhttp3.WebSocketListener {
    public void <init>(...);
}

# ------------------------------------------------------------------------------
# Kotlinx Coroutines
# ------------------------------------------------------------------------------
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ------------------------------------------------------------------------------
# Jetpack Compose
# ------------------------------------------------------------------------------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ------------------------------------------------------------------------------
# Coil
# ------------------------------------------------------------------------------
-keep class coil.** { *; }
-dontwarn coil.**
