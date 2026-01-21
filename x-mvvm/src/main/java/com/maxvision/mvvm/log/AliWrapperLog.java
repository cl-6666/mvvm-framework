package com.maxvision.mvvm.log;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cl.zlog.LogConfiguration;
import com.cl.zlog.LogLevel;
import com.cl.zlog.ZLog;
import com.cl.zlog.flattener.PatternFlattener;
import com.cl.zlog.printer.AndroidPrinter;
import com.cl.zlog.printer.ConsolePrinter;
import com.cl.zlog.printer.Printer;
import com.cl.zlog.printer.file.FilePrinter;
import com.cl.zlog.printer.file.backup.NeverBackupStrategy;
import com.cl.zlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.cl.zlog.printer.file.naming.FileNameGenerator;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ZLog 包装器类（优化版）
 * 
 * 优化内容：
 * 1. 线程安全：使用 volatile 保证可见性
 * 2. 日志级别：支持 Debug/Release 自动切换
 * 3. 文件清理：支持自动清理策略
 * 4. 格式化日志：支持 String.format 风格
 * 5. TAG 可配置：支持自定义 TAG
 * 6. 空安全：参数空检查
 * 7. Console 输出：Debug 模式下支持控制台输出
 * 8. 单参数方法：支持不传 TAG 的便捷方法
 * 
 * @author cl
 * @since 3.2.0
 */
public class AliWrapperLog {
    private static String TAG = "MVVM";  // 默认 TAG
    private static final String FLATTENER = "{d yyyy-MM-dd HH:mm:ss.SSS} {l}/{t}: {m}";
    private static volatile boolean mIsInitialized = false;  // 使用 volatile 保证线程安全
    private static volatile boolean mIsEnabled = false;      // 日志开关
    private static File mLogDir = null;  // 日志目录

    /**
     * 初始化 ZLog
     *
     * @param application    应用程序上下文
     * @param enableLog      是否启用日志
     * @param customTag      自定义 TAG，为空则使用默认值
     * @param folderPath     日志文件保存路径，为空则使用默认路径
     * @param retentionDays  日志保留天数，<=0 表示不自动清理
     */
    public static void init(Application application, boolean enableLog, 
                           @Nullable String customTag, @Nullable String folderPath, 
                           int retentionDays) {
        if (mIsInitialized) {
            w("INIT", "ZLog 已初始化，跳过重复初始化");
            return;
        }

        // 设置自定义 TAG
        if (!TextUtils.isEmpty(customTag)) {
            TAG = customTag;
        }

        // 日志配置
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)
                .tag(TAG)
                .disableStackTrace()
                .build();

        // 创建输出器列表
        List<Printer> printers = new ArrayList<>();
        
        // 1. Android Logcat 打印器
        printers.add(new AndroidPrinter(true));
        // 3. 文件打印器
        if (enableLog) {
            // 确定日志文件保存路径
            String path = TextUtils.isEmpty(folderPath) ?
                    (application.getExternalFilesDir(null) != null ?
                            application.getExternalFilesDir(null).getPath() : null) : folderPath;
            path = TextUtils.isEmpty(path) ? application.getFilesDir().getPath() : path;
            
            mLogDir = new File(path);

            // 构建文件打印器
            FilePrinter.Builder builder = new FilePrinter.Builder(path)
                    .fileNameGenerator(new MyFileNameGenerator(application))
                    .backupStrategy(new NeverBackupStrategy())  // 不备份
                    .flattener(new MyFlattener(FLATTENER));
            
            // 如果设置了保留天数，添加清理策略
            if (retentionDays > 0) {
                long retentionMillis = retentionDays * 24L * 60L * 60L * 1000L;
                builder.cleanStrategy(new FileLastModifiedCleanStrategy(retentionMillis));
            }
            
            printers.add(builder.build());
        }

        // 初始化 ZLog
        ZLog.init(config, printers.toArray(new Printer[0]));
        mIsInitialized = true;
        // 修复：只要初始化了，就允许通过，具体由 Printer 决定是否输出
        // enableLog 参数主要用于控制文件日志
        mIsEnabled = true; 

        // 输出初始化信息
        i("INIT", "╔════════════════════════════════════════════");
        i("INIT", "║  ZLog 初始化成功");
        i("INIT", "║  文件日志: " + (enableLog ? "已启用" : "已禁用"));
        if (enableLog && mLogDir != null) {
            i("INIT", "║  日志目录: " + mLogDir.getAbsolutePath());
            if (retentionDays > 0) {
                i("INIT", "║  保留天数: " + retentionDays + " 天");
            }
        }
        i("INIT", "╚════════════════════════════════════════════");

        // 如果日志开关打开，自动收集设备信息
        if (enableLog) {
            collectDeviceInfo(application);
        }
    }

    /**
     * 简化的初始化方法（使用默认参数）
     *
     * @param application 应用程序上下文
     * @param enableLog   是否启用日志
     */
    public static void init(Application application, boolean enableLog) {
        init(application, enableLog, null, null, 7);
    }

    /**
     * 收集设备信息并记录到日志
     *
     * @param context 应用上下文
     */
    private static void collectDeviceInfo(Context context) {
        try {
            StringBuilder deviceInfo = new StringBuilder();
            deviceInfo.append("\n");
            deviceInfo.append("╔══════════════ 设备信息 ══════════════\n");

            // 应用信息
            try {
                String versionName = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
                int versionCode = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionCode;
                deviceInfo.append("║  应用版本: ").append(versionName)
                        .append(" (").append(versionCode).append(")\n");
                deviceInfo.append("║  包名: ").append(context.getPackageName()).append("\n");
            } catch (PackageManager.NameNotFoundException e) {
                deviceInfo.append("║  应用版本: 获取失败\n");
            }

            deviceInfo.append("║\n");

            // 设备基本信息
            deviceInfo.append("║  设备品牌: ").append(Build.BRAND).append("\n");
            deviceInfo.append("║  设备厂商: ").append(Build.MANUFACTURER).append("\n");
            deviceInfo.append("║  设备型号: ").append(Build.MODEL).append("\n");
            deviceInfo.append("║  设备名称: ").append(Build.DEVICE).append("\n");

            deviceInfo.append("║\n");

            // 系统信息
            deviceInfo.append("║  Android 版本: ").append(Build.VERSION.RELEASE).append("\n");
            deviceInfo.append("║  SDK 版本: ").append(Build.VERSION.SDK_INT).append("\n");
            deviceInfo.append("║  系统指纹: ").append(Build.FINGERPRINT).append("\n");

            deviceInfo.append("║\n");

            // 硬件信息
            deviceInfo.append("║  CPU 架构: ").append(Build.CPU_ABI).append("\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                deviceInfo.append("║  支持的 ABI: ");
                String[] abis = Build.SUPPORTED_ABIS;
                for (int i = 0; i < abis.length; i++) {
                    deviceInfo.append(abis[i]);
                    if (i < abis.length - 1) deviceInfo.append(", ");
                }
                deviceInfo.append("\n");
            }

            deviceInfo.append("║\n");

            // 运行时信息
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1024 / 1024;
            long totalMemory = runtime.totalMemory() / 1024 / 1024;
            long freeMemory = runtime.freeMemory() / 1024 / 1024;
            deviceInfo.append("║  最大内存: ").append(maxMemory).append(" MB\n");
            deviceInfo.append("║  已分配内存: ").append(totalMemory).append(" MB\n");
            deviceInfo.append("║  空闲内存: ").append(freeMemory).append(" MB\n");
            deviceInfo.append("║  可用处理器: ").append(runtime.availableProcessors()).append("\n");

            deviceInfo.append("╚═══════════════════════════════════════\n");

            // 记录到日志
            i("DEVICE_INFO", deviceInfo.toString());

        } catch (Exception e) {
            e("DEVICE_INFO", "收集设备信息失败", e);
        }
    }

    // ==================== 日志输出方法 ====================

    /**
     * VERBOSE 级别日志，使用默认的标签
     */
    public static void v(String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.v(msg);
    }

    /**
     * VERBOSE 级别日志，带标签
     */
    public static void v(String tag, String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.tag(tag).v(msg);
    }

    /**
     * VERBOSE 级别日志（格式化）,使用默认的标签
     */
    public static void v(String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.v(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * VERBOSE 级别日志（格式化）,带标签
     */
    public static void v(String tag, String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.tag(tag).v(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * DEBUG 级别日志，使用默认的标签
     */
    public static void d(String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.d(msg);
    }

    /**
     * DEBUG 级别日志，带标签
     */
    public static void d(String tag, String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.tag(tag).d(msg);
    }

    /**
     * DEBUG 级别日志（格式化）,使用默认的标签
     */
    public static void d(String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.d(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * DEBUG 级别日志（格式化）,带标签
     */
    public static void d(String tag, String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.tag(tag).d(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * INFO 级别日志，使用默认的标签
     */
    public static void i(String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.i(msg);
    }

    /**
     * INFO 级别日志，带标签
     */
    public static void i(String tag, String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.tag(tag).i(msg);
    }


    /**
     * INFO 级别日志（格式化）,使用默认的标签
     */
    public static void i(String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.i(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * INFO 级别日志（格式化）,带标签
     */
    public static void i(String tag, String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.tag(tag).i(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * WARNING 级别日志，使用默认的标签
     */
    public static void w(String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.w(msg);
    }

    /**
     * WARNING 级别日志，带标签
     */
    public static void w(String tag, String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.tag(tag).w(msg);
    }

    /**
     * WARNING 级别日志（格式化）,使用默认的标签
     */
    public static void w(String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.w(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * WARNING 级别日志（格式化）,带标签
     */
    public static void w(String tag, String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.tag(tag).w(String.format(Locale.US, format, args));
        } catch (Exception e) {
            e("LOG", "日志格式化失败: " + format, e);
        }
    }

    /**
     * WARNING 级别日志（带异常）,带标签
     */
    public static void w(String tag, String msg, Throwable throwable) {
        if (!isLogEnabled()) return;
        ZLog.tag(tag).w((msg != null ? msg : ""), throwable);
    }

    /**
     * WARNING 级别日志（仅异常）,使用默认的标签
     */
    public static void w(Throwable throwable) {
        if (!isLogEnabled()) return;
        ZLog.w(throwable);
    }

    /**
     * WARNING 级别日志（仅异常）,带标签
     */
    public static void w(String tag, Throwable throwable) {
        if (!isLogEnabled()) return;
        ZLog.tag(tag).w(throwable);
    }

    /**
     * ERROR 级别日志，使用默认的标签
     */
    public static void e(String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.e(msg);
    }

    /**
     * ERROR 级别日志，带标签
     */
    public static void e(String tag, String msg) {
        if (!isLogEnabled() || msg == null) return;
        ZLog.tag(tag).e(msg);
    }

    /**
     * ERROR 级别日志（格式化）,使用默认的标签
     */
    public static void e(String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.e(String.format(Locale.US, format, args));
        } catch (Exception e) {
            ZLog.e("[LOG] 日志格式化失败: " + format, e);
        }
    }

    /**
     * ERROR 级别日志（格式化）,带标签
     */
    public static void e(String tag, String format, Object... args) {
        if (!isLogEnabled() || format == null) return;
        try {
            ZLog.tag(tag).e(String.format(Locale.US, format, args));
        } catch (Exception e) {
            ZLog.e("[LOG] 日志格式化失败: " + format, e);
        }
    }

    /**
     * ERROR 级别日志（带异常）,使用默认的标签
     */
    public static void e(String msg, Throwable throwable) {
        if (!isLogEnabled()) return;
        ZLog.e((msg != null ? msg : ""), throwable);
    }

    /**
     * ERROR 级别日志（带异常）,带标签
     */
    public static void e(String tag, String msg, Throwable throwable) {
        if (!isLogEnabled()) return;
        ZLog.tag(tag).e((msg != null ? msg : ""), throwable);
    }


    /**
     * JSON 格式日志,使用默认的标签
     */
    public static void json(String jsonStr) {
        if (!isLogEnabled() || jsonStr == null) return;
        ZLog.json(jsonStr);
    }

    /**
     * JSON 格式日志
     */
    public static void json(String tag, String jsonStr) {
        if (!isLogEnabled() || jsonStr == null) return;
        ZLog.tag(tag).json(jsonStr);
    }

    /**
     * XML 格式日志,使用默认的标签
     */
    public static void xml(String xmlStr) {
        if (!isLogEnabled() || xmlStr == null) return;
        ZLog.xml(xmlStr);
    }

    /**
     * XML 格式日志
     */
    public static void xml(String tag, String xmlStr) {
        if (!isLogEnabled() || xmlStr == null) return;
        ZLog.tag(tag).xml(xmlStr);
    }

    // ==================== 工具方法 ====================

    /**
     * 检查日志是否启用
     */
    private static boolean isLogEnabled() {
        if (!mIsInitialized) {
            System.err.println("[" + TAG + "] ZLog 未初始化，请先调用 init() 方法");
            return false;
        }
        return mIsEnabled;
    }

    /**
     * 获取日志目录
     */
    @Nullable
    public static File getLogDir() {
        return mLogDir;
    }

    /**
     * 清空所有日志文件
     */
    public static void clearLogs() {
        if (mLogDir != null && mLogDir.exists() && mLogDir.isDirectory()) {
            File[] files = mLogDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".log")) {
                        if (file.delete()) {
                            d("LOG", "已删除日志文件: " + file.getName());
                        }
                    }
                }
            }
            i("LOG", "日志文件清理完成");
        }
    }

    /**
     * 获取日志文件列表
     */
    @NonNull
    public static List<File> getLogFiles() {
        List<File> logFiles = new ArrayList<>();
        if (mLogDir != null && mLogDir.exists() && mLogDir.isDirectory()) {
            File[] files = mLogDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".log")) {
                        logFiles.add(file);
                    }
                }
            }
        }
        return logFiles;
    }

    /**
     * 获取日志文件总大小（字节）
     */
    public static long getLogFilesSize() {
        long totalSize = 0;
        List<File> files = getLogFiles();
        for (File file : files) {
            totalSize += file.length();
        }
        return totalSize;
    }

    // ==================== 内部类 ====================

    /**
     * 自定义文件名生成器
     * 生成格式：TAG_v版本号_日期.log
     */
    static class MyFileNameGenerator implements FileNameGenerator {
        private final Context mCtx;

        public MyFileNameGenerator(Context context) {
            this.mCtx = context;
        }

        private final ThreadLocal<SimpleDateFormat> mLocalDateFormat = new ThreadLocal<SimpleDateFormat>() {
            @NonNull
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            }
        };

        @Override
        public boolean isFileNameChangeable() {
            return true;
        }

        @Override
        public String generateFileName(int logLevel, long timestamp) {
            SimpleDateFormat sdf = mLocalDateFormat.get();
            if (sdf != null) {
                sdf.setTimeZone(TimeZone.getDefault());
                String dateStr = sdf.format(new Date(timestamp));
                return TAG + "_v" + getVerName() + "_" + dateStr + ".log";
            }
            return TAG + "_" + timestamp + ".log";
        }

        /**
         * 获取应用版本名
         */
        private String getVerName() {
            String verName = "1.0.0";
            try {
                verName = mCtx.getPackageManager()
                        .getPackageInfo(mCtx.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return verName;
        }
    }

    /**
     * 自定义格式化器
     * 使用指定的模式格式化日志
     */
    static class MyFlattener extends PatternFlattener {
        public MyFlattener(String pattern) {
            super(pattern);
        }
    }
}
