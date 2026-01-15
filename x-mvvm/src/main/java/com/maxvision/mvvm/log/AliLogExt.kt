package com.maxvision.mvvm.log

/**
 * AliWrapperLog 的 Kotlin 扩展
 * 
 * 提供更符合 Kotlin 习惯的 API
 * 
 * 使用示例：
 * ```kotlin
 * // 基础日志
 * logD("MainActivity", "Activity 创建")
 * logI("UserViewModel", "用户加载完成")
 * logW("NetworkManager", "网络连接不稳定")
 * logE("ApiService", "请求失败", exception)
 * 
 * // 使用默认 TAG
 * logD("调试信息")
 * logI("普通信息")
 * 
 * // 格式化日志
 * logD("UserViewModel", "用户: %s, 年龄: %d", "张三", 25)
 * 
 * // JSON/XML
 * logJson("API", """{"code":200}""")
 * logXml("XML", "<user><name>张三</name></user>")
 * ```
 * 
 * @author cl
 * @since 3.2.0
 */

// ==================== 基础日志方法 ====================

/**
 * VERBOSE 日志
 */
fun logV(tag: String, msg: String) {
    AliWrapperLog.v(tag, msg)
}

/**
 * VERBOSE 日志（格式化）
 */
fun logV(tag: String, format: String, vararg args: Any?) {
    AliWrapperLog.v(tag, format, *args)
}

/**
 * DEBUG 日志
 */
fun logD(tag: String, msg: String) {
    AliWrapperLog.d(tag, msg)
}

/**
 * DEBUG 日志（格式化）
 */
fun logD(tag: String, format: String, vararg args: Any?) {
    AliWrapperLog.d(tag, format, *args)
}

/**
 * INFO 日志
 */
fun logI(tag: String, msg: String) {
    AliWrapperLog.i(tag, msg)
}

/**
 * INFO 日志（格式化）
 */
fun logI(tag: String, format: String, vararg args: Any?) {
    AliWrapperLog.i(tag, format, *args)
}

/**
 * WARNING 日志
 */
fun logW(tag: String, msg: String) {
    AliWrapperLog.w(tag, msg)
}

/**
 * WARNING 日志（格式化）
 */
fun logW(tag: String, format: String, vararg args: Any?) {
    AliWrapperLog.w(tag, format, *args)
}

/**
 * WARNING 日志（带异常）
 */
fun logW(tag: String, msg: String, throwable: Throwable) {
    AliWrapperLog.w(tag, msg, throwable)
}

/**
 * WARNING 日志（仅异常）
 */
fun logW(tag: String, throwable: Throwable) {
    AliWrapperLog.w(tag, throwable)
}

/**
 * ERROR 日志
 */
fun logE(tag: String, msg: String) {
    AliWrapperLog.e(tag, msg)
}

/**
 * ERROR 日志（格式化）
 */
fun logE(tag: String, format: String, vararg args: Any?) {
    AliWrapperLog.e(tag, format, *args)
}

/**
 * ERROR 日志（带异常）
 */
fun logE(tag: String, msg: String, throwable: Throwable) {
    AliWrapperLog.e(tag, msg, throwable)
}

// ==================== 便捷方法（使用默认 TAG） ====================

/**
 * DEBUG 日志（使用默认 TAG）
 */
fun logD(msg: String) {
    AliWrapperLog.d(msg)
}

/**
 * INFO 日志（使用默认 TAG）
 */
fun logI(msg: String) {
    AliWrapperLog.i(msg)
}

/**
 * WARNING 日志（使用默认 TAG）
 */
fun logW(msg: String) {
    AliWrapperLog.w(msg)
}

/**
 * ERROR 日志（使用默认 TAG）
 */
fun logE(msg: String) {
    AliWrapperLog.e(msg)
}

/**
 * ERROR 日志（使用默认 TAG，带异常）
 */
fun logE(msg: String, throwable: Throwable) {
    AliWrapperLog.e(msg, throwable)
}

// ==================== JSON/XML ====================

/**
 * JSON 格式日志
 */
fun logJson(tag: String, json: String) {
    AliWrapperLog.json(tag, json)
}

/**
 * XML 格式日志
 */
fun logXml(tag: String, xml: String) {
    AliWrapperLog.xml(tag, xml)
}

// ==================== 扩展属性 ====================

/**
 * 为任意类添加 TAG 属性
 * 
 * 使用示例：
 * ```kotlin
 * class MainActivity : AppCompatActivity() {
 *     fun onCreate() {
 *         logD(TAG, "Activity 创建")
 *     }
 * }
 * ```
 */
val Any.TAG: String
    get() = this.javaClass.simpleName

// ==================== 扩展函数 ====================

/**
 * 为任意对象添加日志扩展方法
 * 
 * 使用示例：
 * ```kotlin
 * class UserViewModel {
 *     fun loadUser() {
 *         logD("开始加载用户")        // 自动使用类名作为 TAG
 *         logI("用户加载完成")
 *         logE("加载失败", exception)
 *     }
 * }
 * ```
 */

/**
 * DEBUG 日志（自动使用类名作为 TAG）
 */
fun Any.logD(msg: String) {
    AliWrapperLog.d(this.javaClass.simpleName, msg)
}

/**
 * DEBUG 日志（格式化，自动使用类名作为 TAG）
 */
fun Any.logD(format: String, vararg args: Any?) {
    AliWrapperLog.d(this.javaClass.simpleName, format, *args)
}

/**
 * INFO 日志（自动使用类名作为 TAG）
 */
fun Any.logI(msg: String) {
    AliWrapperLog.i(this.javaClass.simpleName, msg)
}

/**
 * INFO 日志（格式化，自动使用类名作为 TAG）
 */
fun Any.logI(format: String, vararg args: Any?) {
    AliWrapperLog.i(this.javaClass.simpleName, format, *args)
}

/**
 * WARNING 日志（自动使用类名作为 TAG）
 */
fun Any.logW(msg: String) {
    AliWrapperLog.w(this.javaClass.simpleName, msg)
}

/**
 * WARNING 日志（格式化，自动使用类名作为 TAG）
 */
fun Any.logW(format: String, vararg args: Any?) {
    AliWrapperLog.w(this.javaClass.simpleName, format, *args)
}

/**
 * WARNING 日志（带异常，自动使用类名作为 TAG）
 */
fun Any.logW(msg: String, throwable: Throwable) {
    AliWrapperLog.w(this.javaClass.simpleName, msg, throwable)
}

/**
 * ERROR 日志（自动使用类名作为 TAG）
 */
fun Any.logE(msg: String) {
    AliWrapperLog.e(this.javaClass.simpleName, msg)
}

/**
 * ERROR 日志（格式化，自动使用类名作为 TAG）
 */
fun Any.logE(format: String, vararg args: Any?) {
    AliWrapperLog.e(this.javaClass.simpleName, format, *args)
}

/**
 * ERROR 日志（带异常，自动使用类名作为 TAG）
 */
fun Any.logE(msg: String, throwable: Throwable) {
    AliWrapperLog.e(this.javaClass.simpleName, msg, throwable)
}

/**
 * JSON 日志（自动使用类名作为 TAG）
 */
fun Any.logJson(json: String) {
    AliWrapperLog.json(this.javaClass.simpleName, json)
}

/**
 * XML 日志（自动使用类名作为 TAG）
 */
fun Any.logXml(xml: String) {
    AliWrapperLog.xml(this.javaClass.simpleName, xml)
}
