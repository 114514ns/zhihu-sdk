package cn.pprocket.zhihu.interceptor;

import cn.pprocket.zhihu.Client;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class EncryptInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        return chain.proceed(chain.request());
    }
    public static String genParam(String url){
        String cookie = Client.INSTANCE.getCookie();
        String[] split = cookie.split(";");
        return "param";
    }
}
