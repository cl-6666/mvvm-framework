package com.cl.test.util

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Enumeration

/**
 * name：cl
 * date：2022/3/17
 * desc： json工具类
 */
object JsonUtils {
    const val EMPTY_JSON = "{}"

    /** 空的 `JSON` 数组(集合)数据 - `"[]"`。  */
    const val EMPTY_JSON_ARRAY = "[]"

    /** 默认的 `JSON` 日期/时间字段的格式化模式。  */
    const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss SSS"

    /**
     * 直接JSON转类，好像jsonString只要不为空，就有对象返回
     * 看来还得判断转换类的其他字段
     */
    fun <T> parseJson(jsonString: String?, cls: Class<T>?): T? {
        if (TextUtils.isEmpty(jsonString)) {
            return null
        }
        var t: T? = null
        try {
            val gson = Gson()
            t = gson.fromJson(jsonString, cls)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return t
    }

    /**
     * 直接JSON转类，好像jsonString只要不为空，就有对象返回
     * 看来还得判断转换类的其他字段
     */
    fun <T> parseJson(jsonString: String?, type: Type?): T? {
        if (TextUtils.isEmpty(jsonString)) {
            return null
        }
        var t: T? = null
        try {
            val gson = Gson()
            t = gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return t
    }

    /**
     * Json转List
     */
    fun <T> jsonToList(jsonString: String?, cls: Class<T>?): ArrayList<T>? {
        if (TextUtils.isEmpty(jsonString)) {
            return null
        }
        // 这是一个组合类型
        val type = object : TypeToken<ArrayList<JsonObject?>?>() {}.type
        val jsonObjS = Gson().fromJson<ArrayList<JsonObject>>(jsonString, type)
        val listOfT = ArrayList<T>()
        for (jsonObj in jsonObjS) {
            listOfT.add(Gson().fromJson(jsonObj, cls))
        }
        return listOfT
    }

    /**
     * Json 转 List
     */
    fun <T> jsonToList2(jsonString: String?, cls: Class<T>?): ArrayList<T>? {
        if (TextUtils.isEmpty(jsonString)) {
            return null
        }
        val list = ArrayList<T>()
        val jsonParser = JsonParser()
        val gson = Gson()
        val rootArray = jsonParser.parse(jsonString).asJsonArray
        for (json in rootArray) {
            try {
                list.add(gson.fromJson(json, cls))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return list
    }

    /**
     * Json 转 ListMap
     */
    fun <T> jsonToListMap(jsonString: String?, cls: Class<T>?): ArrayList<Map<String?, T>?>? {
        if (TextUtils.isEmpty(jsonString)) {
            return null
        }
        var list = ArrayList<Map<String?, T>?>()
        try {
            val gson = Gson()
            list = gson.fromJson(
                jsonString,
                object : TypeToken<List<Map<String?, T>?>?>() {}.type
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    /**
     * 对象转换成json字符串
     */
    @JvmOverloads
    fun toJson(
        target: Any?, targetType: Type? = null,
        isSerializeNulls: Boolean = true, version: Double? = null, datePattern: String? = null,
        excludesFieldsWithoutExpose: Boolean = false
    ): String {
        var datePattern = datePattern
        if (target == null) return EMPTY_JSON
        val builder = GsonBuilder()
        if (isSerializeNulls) builder.serializeNulls()
        if (version != null) builder.setVersion(version.toDouble())
        if (isBlank(datePattern)) datePattern = DEFAULT_DATE_PATTERN
        builder.setDateFormat(datePattern)
        if (excludesFieldsWithoutExpose) builder.excludeFieldsWithoutExposeAnnotation()
        return toJson(target, targetType, builder)
    }

    fun toJson(target: Any?, targetType: Type?, builder: GsonBuilder?): String {
        if (target == null) return EMPTY_JSON
        var gson: Gson? = null
        gson = if (builder == null) {
            Gson()
        } else {
            builder.create()
        }
        var result = EMPTY_JSON
        try {
            result = if (targetType == null) {
                gson!!.toJson(target)
            } else {
                gson!!.toJson(target, targetType)
            }
        } catch (ex: Exception) {
            if (target is Collection<*>
                || target is Iterator<*>
                || target is Enumeration<*>
                || target.javaClass.isArray
            ) {
                result = EMPTY_JSON_ARRAY
            }
        }
        return result
    }

    private fun isBlank(text: String?): Boolean {
        return null == text || "" == text.trim { it <= ' ' }
    }
}