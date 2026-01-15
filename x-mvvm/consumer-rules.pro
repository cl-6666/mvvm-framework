# ====================================================================
# x-mvvm 框架混淆规则
# 版本: 3.2.0
# ====================================================================

# ==================== 基础规则 ====================
# 保留注解
-keepattributes *Annotation*
# 保留泛型信息
-keepattributes Signature
# 保留异常信息
-keepattributes Exceptions
# 保留行号信息（便于调试）
-keepattributes SourceFile,LineNumberTable

# ==================== 框架核心类 ====================
# 保留所有基类
-keep public class com.maxvision.mvvm.base.** { *; }

# 保留 ViewModel 基类
-keep public class com.maxvision.mvvm.base.viewmodel.BaseViewModel {
    public <methods>;
    public <fields>;
}

# 保留 Activity/Fragment 基类
-keep public class com.maxvision.mvvm.base.activity.** {
    public <methods>;
}
-keep public class com.maxvision.mvvm.base.fragment.** {
    public <methods>;
}

# ==================== 网络层 ====================
# 保留 BaseResponse 及其子类
-keep public class com.maxvision.mvvm.network.BaseResponse { *; }
-keep public class * extends com.maxvision.mvvm.network.BaseResponse {
    public <methods>;
    public <fields>;
}

# 保留网络请求相关类
-keep public class com.maxvision.mvvm.network.BaseNetworkApi { *; }
-keep public class com.maxvision.mvvm.network.RequestConfig { *; }
-keep public class com.maxvision.mvvm.network.ApiResult { *; }
-keep public class com.maxvision.mvvm.network.ApiResult$** { *; }

# 保留异常处理类
-keep public class com.maxvision.mvvm.network.AppException {
    public <init>(...);
    public <methods>;
    public <fields>;
}
-keep public class com.maxvision.mvvm.network.ExceptionHandle { *; }
-keep public class com.maxvision.mvvm.network.Error { *; }

# 保留 ResultState
-keep public class com.maxvision.mvvm.network.state.ResultState { *; }
-keep public class com.maxvision.mvvm.network.state.ResultState$** { *; }

# ==================== 回调和 LiveData ====================
# 保留 UnPeekLiveData
-keep public class com.maxvision.mvvm.callback.UnPeekLiveData { *; }
-keep public class com.maxvision.mvvm.callback.ProtectedUnPeekLiveData { *; }

# 保留 EventLiveData
-keep public class com.maxvision.mvvm.callback.livedata.event.EventLiveData { *; }

# 保留类型安全的 LiveData
-keep public class com.maxvision.mvvm.callback.livedata.** { *; }

# 保留 DataBinding ObservableField
-keep public class com.maxvision.mvvm.callback.databind.** { *; }

# ==================== 配置类 ====================
# 保留配置类
-keep public class com.maxvision.mvvm.config.FrameworkConfig {
    public static <fields>;
    public static <methods>;
}

# ==================== 扩展函数 ====================
# 保留扩展函数
-keep public class com.maxvision.mvvm.ext.BaseViewModelExtKt { *; }
-keep public class com.maxvision.mvvm.ext.ViewBindUtilKt { *; }
-keep public class com.maxvision.mvvm.ext.view.** { *; }

# ==================== 生命周期管理 ====================
# 保留生命周期相关类
-keep public class com.maxvision.mvvm.ext.lifecycle.** { *; }

# ==================== 工具类 ====================
# 保留工具类
-keep public class com.maxvision.mvvm.util.** {
    public <methods>;
}

# ==================== 第三方库规则 ====================
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# Retrofit 接口方法
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==================== Hilt ====================
# 保留 Hilt 生成的代码
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$ViewComponentBuilderEntryPoint { *; }

# 保留带 Hilt 注解的类
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @javax.inject.Inject class * { *; }

# 保留 Assisted Injection
-keepclassmembers class * {
    @dagger.assisted.Assisted <init>(...);
}

# 抑制 Hilt 相关警告
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**

# AndroidX Lifecycle
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# ==================== 数据类保护 ====================
# 如果使用 data class 作为网络响应，建议添加：
# -keep class com.your.package.model.** { *; }
# 或使用 @Keep 注解标记需要保留的类

# ==================== 反射相关 ====================
# 保留反射使用的类
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# ==================== R8 优化 ====================
# 允许优化
-allowoptimization

# 移除日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# 移除 Timber 日志（生产环境）
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ==================== 警告抑制 ====================
# 忽略警告（谨慎使用）
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
