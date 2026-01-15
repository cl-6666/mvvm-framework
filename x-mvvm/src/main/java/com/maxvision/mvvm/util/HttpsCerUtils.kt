package com.maxvision.mvvm.util

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * name：cl
 * date：2023/4/13
 * desc：支持Https连接的Okhttp客户端
 */
object HttpsCerUtils {
    private const val CERT_PSW = "M0a0x2v9i9s0on"//如果需要兼容安卓5.0以下，可以使用这句

    //okHttpClientBuilder.sslSocketFactory(new TLSSocketFactory(), trustAllManager);
    //跳过验证
    //信任所有证书
    val trustAllCertificateClient: OkHttpClient
        get() {
            val mBuilder = OkHttpClient.Builder()
            try {
                val sc = SSLContext.getInstance("TLS")
                val trustAllManager: X509TrustManager = object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }
                sc.init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())
                mBuilder.sslSocketFactory(sc.socketFactory, trustAllManager)
                //如果需要兼容安卓5.0以下，可以使用这句
                //okHttpClientBuilder.sslSocketFactory(new TLSSocketFactory(), trustAllManager);
                //跳过验证
                mBuilder.hostnameVerifier { hostname: String?, session: SSLSession? -> true }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mBuilder.build()
        }

    //只信任指定证书（传入字符串）
    fun getCertificateClient(context: Context?, cerStr: String): OkHttpClient {
        val mBuilder = OkHttpClient.Builder()
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val byteArrayInputStream = ByteArrayInputStream(cerStr.toByteArray())
            val ca = certificateFactory.generateCertificate(byteArrayInputStream)
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, CERT_PSW.toCharArray())
            keyStore.setCertificateEntry("ca", ca)
            byteArrayInputStream.close()
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            mBuilder.sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            //mBuilder.hostnameVerifier((hostname, session) -> true);//跳过验证
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mBuilder.build()
    }

    //只信任指定证书（传入raw资源ID）
    fun getCertificateClient(context: Context, cerResID: Int): OkHttpClient {
        val mBuilder = OkHttpClient.Builder()
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val inputStream = context.resources.openRawResource(cerResID)
            val ca = certificateFactory.generateCertificate(inputStream)
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, CERT_PSW.toCharArray())
            keyStore.setCertificateEntry("ca", ca)
            inputStream.close()
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            mBuilder.sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            //mBuilder.hostnameVerifier((hostname, session) -> true);//跳过验证
        } catch (e: Exception) {
            //e.printStackTrace();
            Log.e("TAG", "HttpsCerUtils getCertificateClient 异常了：" + e.message)
            //            CrashReport.postCatchedException(new Throwable("证书异常 ==> HttpsCerUtils getCertificateClient=>>",e));
        }
        return mBuilder.build()
    }

    //批量信任证书
    fun getCertificateClient(context: Context, vararg cerResIDs: Int): OkHttpClient {
        val mBuilder = OkHttpClient.Builder()
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)
            for (i in cerResIDs.indices) {
                val ca = certificateFactory.generateCertificate(
                    context.resources.openRawResource(
                        cerResIDs[i]
                    )
                )
                keyStore.setCertificateEntry("ca$i", ca)
            }
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            mBuilder.sslSocketFactory(
                sslContext.socketFactory,
                tmf.trustManagers[0] as X509TrustManager
            )
            //mBuilder.hostnameVerifier((hostname, session) -> true);//跳过验证
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mBuilder.build()
    }
}