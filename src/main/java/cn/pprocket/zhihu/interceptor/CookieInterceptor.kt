package cn.pprocket.zhihu.interceptor

import cn.pprocket.zhihu.Client
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor :Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var newBuilder = chain.request().newBuilder()
        var addHeader = newBuilder.addHeader("Cookie", Client.cookie)
        return chain.proceed(addHeader.build())
    }
}