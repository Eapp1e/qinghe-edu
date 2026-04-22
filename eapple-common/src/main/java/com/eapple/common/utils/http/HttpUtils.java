package com.eapple.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import com.eapple.common.constant.Constants;
import com.eapple.common.utils.StringUtils;

/**
 * Http 请求工具类。
 *
 * @author Eapp1e
 */
public class HttpUtils
{
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 发送 GET 请求。
     *
     * @param url 请求地址
     * @return 响应内容
     */
    public static String sendGet(String url)
    {
        return sendGet(url, StringUtils.EMPTY);
    }

    /**
     * 发送 GET 请求。
     *
     * @param url 请求地址
     * @param param 请求参数，格式为 name1=value1&name2=value2
     * @return 响应内容
     */
    public static String sendGet(String url, String param)
    {
        return sendGet(url, param, Constants.UTF8);
    }

    /**
     * 发送 GET 请求。
     *
     * @param url 请求地址
     * @param param 请求参数，格式为 name1=value1&name2=value2
     * @param contentType 响应字符集
     * @return 响应内容
     */
    public static String sendGet(String url, String param, String contentType)
    {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try
        {
            String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
            log.info("sendGet - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            log.error("HttpUtils.sendGet ConnectException, url={}, param={}", url, param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("HttpUtils.sendGet SocketTimeoutException, url={}, param={}", url, param, e);
        }
        catch (IOException e)
        {
            log.error("HttpUtils.sendGet IOException, url={}, param={}", url, param, e);
        }
        catch (Exception e)
        {
            log.error("HttpUtils.sendGet Exception, url={}, param={}", url, param, e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                log.error("HttpUtils.sendGet close exception, url={}, param={}", url, param, ex);
            }
        }
        return result.toString();
    }

    /**
     * 发送 POST 请求。
     *
     * @param url 请求地址
     * @param param 请求参数
     * @return 响应内容
     */
    public static String sendPost(String url, String param)
    {
        return sendPost(url, param, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 发送 POST 请求。
     *
     * @param url 请求地址
     * @param param 请求参数
     * @param contentType 请求内容类型
     * @return 响应内容
     */
    public static String sendPost(String url, String param, String contentType)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {
            log.info("sendPost - {}", url);
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            log.error("HttpUtils.sendPost ConnectException, url={}, param={}", url, param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("HttpUtils.sendPost SocketTimeoutException, url={}, param={}", url, param, e);
        }
        catch (IOException e)
        {
            log.error("HttpUtils.sendPost IOException, url={}, param={}", url, param, e);
        }
        catch (Exception e)
        {
            log.error("HttpUtils.sendPost Exception, url={}, param={}", url, param, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                log.error("HttpUtils.sendPost close exception, url={}, param={}", url, param, ex);
            }
        }
        return result.toString();
    }

    /**
     * 发送 HTTPS POST 请求。
     *
     * @param url 请求地址
     * @param param 请求参数
     * @return 响应内容
     */
    public static String sendSSLPost(String url, String param)
    {
        return sendSSLPost(url, param, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 发送 HTTPS POST 请求。
     *
     * @param url 请求地址
     * @param param 请求参数
     * @param contentType 请求内容类型
     * @return 响应内容
     */
    public static String sendSSLPost(String url, String param, String contentType)
    {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;
        try
        {
            log.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";
            while ((ret = br.readLine()) != null)
            {
                if (ret != null && !"".equals(ret.trim()))
                {
                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            log.info("recv - {}", result);
            conn.disconnect();
            br.close();
        }
        catch (ConnectException e)
        {
            log.error("HttpUtils.sendSSLPost ConnectException, url={}, param={}", url, param, e);
        }
        catch (SocketTimeoutException e)
        {
            log.error("HttpUtils.sendSSLPost SocketTimeoutException, url={}, param={}", url, param, e);
        }
        catch (IOException e)
        {
            log.error("HttpUtils.sendSSLPost IOException, url={}, param={}", url, param, e);
        }
        catch (Exception e)
        {
            log.error("HttpUtils.sendSSLPost Exception, url={}, param={}", url, param, e);
        }
        return result.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }
}
