package com.cl.test.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import okhttp3.ResponseBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * name：cl
 * date：2023/4/10
 * desc：文件夹工具类
 */
class FileUtil {
    private object FileUtilTypeClass {
        val instance = FileUtil()
    }

    var pluginRootPath: String? = null
        private set

    fun init(context: Context) {
        pluginRootPath = if (isSDCardMounted) {
            //有外置sdcard 就写到外置sdcard里面
            createFolder(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "/apkPath"
            )
        } else {
            createFolder(context.filesDir.path + File.separator + "/apkPath")
        }
    }

    /**
     * 插件文件夹
     *
     * @param path 需要创建的文件夹路径
     * @return 返回创建的文件夹绝对路径
     */
    fun createFolder(path: String?): String? {
        val file = File(path)
        if (!file.exists()) {
            val mkdirs = file.mkdirs()
            if (!mkdirs) {
                return null
            }
        }
        return file.absolutePath
    }

    fun getFolders(path: String?): Array<File>? {
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        return if (file.isFile) {
            null
        } else file.listFiles()
    }

    /**
     * 拷贝插件到插件目录
     *
     * @param path 插件文件绝对路径
     * @return 返回插件文件绝对路径
     */
    fun copyPluginApk(path: String?): String? {
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        return if (!file.isFile) {
            null
        } else copyPluginApk(file)
    }

    /**
     * 拷贝插件到插件目录
     *
     * @param file 插件文件
     * @return 返回插件文件绝对路径
     */
    fun copyPluginApk(file: File): String? {
        if (!file.exists()) {
            return null
        }
        return if (!file.isFile) {
            null
        } else copyFileToFolder(file, pluginRootPath)
    }

    /**
     * 获取不带后缀的文件名称
     *
     * @param fileName 文件名
     * @return 返回没有后缀的文件名
     */
    fun getFileNameWithoutSuffix(fileName: String): String {
        return fileName.substring(0, fileName.lastIndexOf("."))
    }

    /**
     * 拷贝文件到指定目录
     *
     * @param path       文件路径
     * @param folderPath 目录路径
     * @return 返回拷贝后文件的绝对路径
     */
    fun copyFileToFolder(path: String?, folderPath: String?): String? {
        val file = File(path)
        return if (!file.exists()) {
            null
        } else copyFileToFolder(file, folderPath)
    }

    /**
     * 拷贝文件到指定目录
     *
     * @param file       文件
     * @param folderPath 目录路径
     * @return 返回拷贝后文件的绝对路径
     */
    fun copyFileToFolder(file: File, folderPath: String?): String? {
        try {
            val inputStream = FileInputStream(file)
            val createFile = File(folderPath, file.name)
            if (!createFile.exists()) {
                val createFileNewFile = createFile.createNewFile()
                if (!createFileNewFile) {
                    return null
                }
            }
            val outputStream = FileOutputStream(createFile)
            val data = ByteArray(1024)
            var length: Int
            while (inputStream.read(data).also { length = it } != -1) {
                outputStream.write(data, 0, length)
            }
            inputStream.close()
            outputStream.flush()
            outputStream.close()
            return createFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getUriFile(context: Context, uri: Uri): File? {
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            return File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val contentResolver = context.contentResolver
            val sb = StringBuilder()
            sb.append("temp")
            sb.append(".")
            sb.append(
                MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
            )
            val inputStream: InputStream?
            try {
                inputStream = contentResolver.openInputStream(uri)
                val absolutePath = context.cacheDir.absolutePath
                val catchFolder = File(absolutePath)
                val files = catchFolder.listFiles()!!
                for (f in files) {
                    f.delete()
                }
                val file = File(absolutePath, sb.toString())
                val outputStream = FileOutputStream(file)
                val data = ByteArray(1024)
                while (inputStream!!.read(data) != -1) {
                    outputStream.write(data)
                }
                outputStream.close()
                inputStream.close()
                return file
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun deleteFile(path: String?) {
        deleteFile(File(path), false)
    }

    fun deleteFile(path: String?, isDeleteFolder: Boolean) {
        deleteFile(File(path), isDeleteFolder)
    }

    @JvmOverloads
    fun deleteFile(file: File, isDeleteFolder: Boolean = false) {
        if (!file.exists()) {
            return
        }
        if (file.isFile) {
            file.delete()
            return
        }
        val files = file.listFiles()
        for (itemFile in files) {
            deleteFile(itemFile.absolutePath, isDeleteFolder)
        }
        if (isDeleteFolder) {
            file.delete()
        }
    }

    /**
     * 下载到本地
     *
     * @param body 内容
     * @return 成功或者失败
     */
    fun writeResponseBodyToDisk(body: ResponseBody, path: String, filename: String): Boolean {
        return try {
            //判断文件夹是否存在
            val files = File(path) //跟目录一个文件夹
            if (!files.exists()) {
                //不存在就创建出来
                files.mkdirs()
            }
            //创建一个文件
            val futureStudioIconFile = File(path + filename)
            //初始化输入流
            var inputStream: InputStream? = null
            //初始化输出流
            var outputStream: OutputStream? = null
            try {
                //设置每次读写的字节
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                //请求返回的字节流
                inputStream = body.byteStream()
                //创建输出流
                outputStream = FileOutputStream(futureStudioIconFile)
                //进行读取操作
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    //进行写入操作
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                //刷新
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    companion object {
        val instance: FileUtil
            get() = FileUtilTypeClass.instance

        /**
         * sdcard存在并可写
         * sdcard未被移除
         */
        private val isSDCardMounted: Boolean
            private get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()
    }
}