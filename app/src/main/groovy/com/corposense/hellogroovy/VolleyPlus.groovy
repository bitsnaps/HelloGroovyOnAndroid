package com.corposense.hellogroovy


import android.content.Context
import android.graphics.drawable.BitmapDrawable
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.cache.plus.ImageCache
import com.android.volley.cache.plus.ImageLoader
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import android.support.v4.util.LruCache
import groovy.transform.Synchronized

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

/*
usage:
VolleyPlus.getInstance(ctx).getRequestQueue().add(request)
 */
class VolleyPlus {

    private static VolleyPlus mInstance
    private RequestQueue mRequestQueue
    private ImageLoader imageLoader
    private static Context mCtx
    private static boolean clearCache

    private VolleyPlus(Context context) {
        mCtx = context
        mRequestQueue = getRequestQueue()


        imageLoader = new ImageLoader(mRequestQueue,
                new /*ImageLoader.*/ImageCache() {
                    private final LruCache<String, /*Bitmap*/BitmapDrawable> cache = new LruCache<String, /*Bitmap*/BitmapDrawable>(20)

                    @Override
                    /*Bitmap*/BitmapDrawable getBitmap(String url) {
                        cache.get(url)
                    }

                    @Override
                    void putBitmap(String url, /*Bitmap*/BitmapDrawable bitmap) {
                        cache.put(url, bitmap)
                    }

                    @Override
                    void clear() {
                        cache.evictAll()
                    }

                    @Override
                    void invalidateBitmap(String url) {
                        cache.remove(url)
                    }
                })
    }

    @Synchronized
    static VolleyPlus getInstance(Context context, boolean clearCache = false) {
        this.clearCache = clearCache
        if (mInstance == null) {
            mInstance = new VolleyPlus(context)
        }
        mInstance
    }

    RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext())
            // if you find an issue with https certificate you enable this line
            // then add "useLibrary 'org.apache.http.legacy'" to your gradle.build android{} section
            //mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new HurlStack(null, sslSocketFactoryProvider()))

        }
        mRequestQueue;
    }

    /**
     * Provides a SSLSocketFactory that accepts Self Signed Certificates
     *
     * @return
     *
     private SSLSocketFactory sslSocketFactoryProvider(){
         final TrustManager[] certs = [new X509TrustManager() {
             @Override
             public X509Certificate[] getAcceptedIssuers() {
                 return null
             }
             @Override
             public void checkServerTrusted(final X509Certificate[] chain,
                                            final String authType) throws CertificateException {
             }
             @Override
             public void checkClientTrusted(final X509Certificate[] chain,
                                            final String authType) throws CertificateException {
             }
         }]
         final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
             @Override
             public boolean verify(String hostname, SSLSession session) {
                 return true
             }
         }
         HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
         SSLContext ctx = null
         try {
             ctx = SSLContext.getInstance("TLS")
             ctx.init(null, certs, new SecureRandom())
         } catch (final java.security.GeneralSecurityException ex) {
         }
         return  ctx.getSocketFactory()
     }*/

    public<T> void addToRequestQueue(Request<T> req, boolean shouldCache = true) {
        RequestQueue requestQueue = getRequestQueue()
        if (clearCache){
            requestQueue.cache.clear()
        }
        if (!shouldCache){
            req.shouldCache = false
        }
        requestQueue.add(req)
    }

}