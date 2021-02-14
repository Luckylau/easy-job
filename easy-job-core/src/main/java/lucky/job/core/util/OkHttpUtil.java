package lucky.job.core.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import okhttp3.*;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by luckylau on 2018/7/25
 * Http请求工具类，基于okhttp3实现
 */
public class OkHttpUtil {

    public static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=UTF-8");
    public static final MediaType TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
    private final static Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);


    /**
     * 有代理单例
     */
    private static OkHttpClient client_1 = null;

    /**
     * 无代理单例
     */
    private static OkHttpClient client_2 = null;

    /**
     * https
     */
    private static OkHttpClient client_3 = null;

    public static OkHttpClient getInstance(String proxyIp, Integer proxyPort, Integer timeout) {
        if (client_1 == null) {
            synchronized (OkHttpUtil.class) {
                if (client_1 == null) {
                    if (timeout == null) {
                        timeout = 20;
                    }
                    client_1 = new OkHttpClient.Builder()
                            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort)))
                            .connectionPool(new ConnectionPool(200, 5, TimeUnit.SECONDS))
                            .readTimeout(timeout, TimeUnit.SECONDS).connectTimeout(timeout, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return client_1;
    }

    public static OkHttpClient getInstance() {
        if (client_2 == null) {
            synchronized (OkHttpUtil.class) {
                if (client_2 == null) {
                    client_2 = new OkHttpClient.Builder()
                            .connectionPool(new ConnectionPool(100, 5, TimeUnit.SECONDS))
                            .readTimeout(20, TimeUnit.SECONDS)
                            .connectTimeout(20, TimeUnit.SECONDS).build();
                }
            }
        }
        return client_2;
    }

    public static OkHttpClient getHttpsInstance() {
        if (client_3 == null) {
            synchronized (OkHttpUtil.class) {
                if (client_3 == null) {
                    X509TrustManager manager = SSLSocketClient.getX509TrustManager();
                    client_3 = new OkHttpClient.Builder()
                            .sslSocketFactory(SSLSocketClient.getSocketFactory(manager), manager)
                            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                            .connectionPool(new ConnectionPool(100, 5, TimeUnit.SECONDS))
                            .readTimeout(20, TimeUnit.SECONDS)
                            .connectTimeout(20, TimeUnit.SECONDS).build();
                }
            }
        }
        return client_3;
    }

    public static void main(String[] args) {
        System.out.println(OkHttpUtil.get("http://www.baidu.com"));
    }

    /**
     * 提交表单  application/x-www-form-urlencoded; charset=utf-8
     *
     * @param url
     * @param formMap
     * @return
     */
    public static String postForm(String url, Map<String, String> formMap) {
        return postForm(url, null, formMap, null);
    }

    public static String postForm(String url, Map<String, String> headerMap, String body) {
        try {
            Response response = execute(url, null, headerMap, TYPE_FORM, body, "POST", null, null, null);
            return response == null ? null : response.body().string();
        } catch (IOException e) {
            logger.error("postForm", e);
        }
        return null;
    }

    public static String postForm(String url, Map<String, String> headerMap, Map<String, String> formMap, Integer timeout) {
        Preconditions.checkArgument(formMap != null, "formMap is null");
        String body = formMap.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
        try {
            Response response = execute(url, null, headerMap, TYPE_FORM, body, "POST", timeout, null, null);
            return response == null ? null : response.body().string();
        } catch (IOException e) {
            logger.error("postForm", e);
        }
        return null;
    }

    /**
     * 提交json application/json; charset=UTF-8
     *
     * @param url
     * @param body
     * @return
     */
    public static String postJson(String url, String body) {
        return postJson(url, null, body, null, null, null);
    }

    public static String putJson(String url, String body) {
        try {
            Response response = execute(url, null, null, TYPE_JSON, body, "PUT", null, null, null);
            return response == null ? null : response.body().string();
        } catch (IOException e) {
            logger.error("putJson", e);
        }
        return null;
    }

    public static String delete(String url) {
        try {
            Response response = execute(url, null, null, TYPE_JSON, null, "DELETE", null, null, null);
            return response == null ? null : response.body().string();
        } catch (IOException e) {
            logger.error("delete", e);
        }
        return null;
    }

    public static String postJson(String url, String body, Integer timeout) {
        return postJson(url, null, body, timeout, null, null);
    }

    public static String postJson(String url, String body, String proxyIp, Integer proxyPort) {
        return postJson(url, null, body, null, null, null);
    }

    public static String postJson(String url, Map<String, String> headerMap, String body) {
        try {
            Response response = execute(url, null, headerMap, MediaType.parse(headerMap.get("Content-Type")), body, "POST", null, null, null);
            return response == null ? null : response.body().string();
        } catch (IOException e) {
            logger.error("postJson", e);
        }
        return null;
    }

    private static String postJson(String url, Map<String, String> headerMap, String body, Integer timeout, String proxyIp, Integer proxyPort) {
        try {
            Response response = execute(url, null, headerMap, TYPE_JSON, body, "POST", timeout, proxyIp, proxyPort);
            return response == null ? null : response.body().string();
        } catch (IOException e) {
            logger.error("postJson", e);
        }
        return null;
    }

    /**
     * 提交json application/json; charset=UTF-8
     *
     * @param url
     * @param body
     * @return
     */
    public static String postJson(String url, String body, String proxyIp, Integer proxyPort, Integer timeout) {
        return postJson(url, null, body, timeout, proxyIp, proxyPort);
    }


    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> queryMap) {
        return get(url, queryMap, null);
    }

    public static String get(String url, Map<String, String> queryMap, Map<String, String> headerMap) {
        try {
            Response response = execute(url, queryMap, headerMap, TYPE_JSON, null, "GET", null, null, null);
            return response == null ? null : response.body().string();
        } catch (IOException e) {
            logger.error("get", e);
        }
        return null;
    }

    public static Response doGet(String url, Map<String, String> queryMap) {
        return execute(url, queryMap, null, TYPE_JSON, null, "GET", null, null, null);
    }

    public static Response doGet(String url, Map<String, String> queryMap, Map<String, String> headerMap) {
        return execute(url, queryMap, headerMap, MediaType.parse(headerMap.get("Content-Type")), null, "GET", null, null, null);
    }

    public static Response doGet(String url) {
        return execute(url, null, null, TYPE_JSON, null, "GET", null, null, null);
    }

    public static Response doPostJson(String url, String body) {
        return execute(url, null, null, TYPE_JSON, body, "POST", null, null, null);
    }

    private static Response execute(String url, Map<String, String> queryMap, Map<String, String> headerMap, MediaType mediaType, String body, String method, Integer timeout, String proxyIp, Integer proxyPort) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "url is null");

        Request.Builder builder = new Request.Builder();
        //url添加查询参数
        if (queryMap != null && !queryMap.isEmpty()) {
            HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
            queryMap.forEach(httpUrlBuilder::addQueryParameter);
            builder.url(httpUrlBuilder.build());
        } else {
            builder.url(url);
        }
        //添加header
        if (headerMap != null && !headerMap.isEmpty()) {
            headerMap.forEach(builder::addHeader);
        }

        //添加执行方法和请求体
        if (ArrayUtils.contains(new String[]{"POST", "PUT", "DELETE", "PATCH"}, method)) {
            RequestBody requestBody;
            if (body != null) {
                requestBody = RequestBody.create(mediaType, body);
            } else {
                requestBody = RequestBody.create(mediaType, "");
            }
            builder.method(method, requestBody);
        } else if (Objects.equals(method.toUpperCase(), "GET")) {
            builder.method("GET", null);
        } else {
            throw new IllegalArgumentException(String.format("http method:%s not support!", method));
        }


        try {
            OkHttpClient client;
            if (!Strings.isNullOrEmpty(proxyIp) && proxyPort != null) {
                client = getInstance(proxyIp, proxyPort, timeout);
            } else {
                client = getHttpsInstance();
            }

            return client.newCall(builder.build()).execute();
        } catch (IOException e) {
            logger.error("http {} 请求异常，url:{}, body:{}", method, url, body, e);
            return null;
        }

    }

}
