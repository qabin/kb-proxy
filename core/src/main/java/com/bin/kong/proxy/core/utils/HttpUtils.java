package com.bin.kong.proxy.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.zip.GZIPInputStream;

@Slf4j
public class HttpUtils {

    public static Object doPost(String url, Map<String, String> paramsMap, Map<String, String> headersMap, String body) throws Exception {
        CloseableHttpClient httpClient = getHttpClient(url);
        HttpPost httpPost = new HttpPost(url);
        addConfig(httpPost);
        addHeaderToRequest(headersMap, httpPost);
        try {
            addBody(httpPost, paramsMap, headersMap, body);
            HttpResponse response = httpClient.execute(httpPost);
            return dealHttpResponse(response);

        } catch (Exception e) {
            log.error("请求异常：" + e);
            return null;
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error("请求异常：" + e);
                }
            }
        }
    }

    public static Object doGet(String url, Map<String, String> params, Map<String, String> headersMap) throws Exception {
        CloseableHttpClient httpClient = getHttpClient(url);
        HttpGet httpGet;
        try {
            if (params != null && params.size() > 0) {
                URIBuilder builder = new URIBuilder(url);
                Set<String> set = params.keySet();
                for (String key : set) {
                    builder.setParameter(key, String.valueOf(params.get(key)));
                }
                httpGet = new HttpGet(builder.build());
            } else {
                httpGet = new HttpGet(url);
            }
            addConfig(httpGet);
            addHeaderToRequest(headersMap, httpGet);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            return dealHttpResponse(httpResponse);
        } catch (Exception ex) {
            log.error("请求异常：" + ex.getMessage());
            return null;
        } finally {
            try {
                if (null != httpClient) {
                    httpClient.close();
                }

            } catch (IOException e) {
                log.error("请求异常：" + e);
            }
        }
    }

    public static Object doPut(String url, Map<String, String> paramsMap, Map<String, String> headersMap, String body) throws Exception {
        CloseableHttpClient httpClient = getHttpClient(url);
        HttpPut httpPut;
        try {
            httpPut = new HttpPut(url);
            addConfig(httpPut);
            addHeaderToRequest(headersMap, httpPut);
            addBody(httpPut, paramsMap, headersMap, body);
            CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
            return dealHttpResponse(httpResponse);
        } catch (Exception ex) {
            log.error("请求异常：" + ex.getMessage());
            return null;
        } finally {
            try {
                if (null != httpClient) {
                    httpClient.close();
                }

            } catch (IOException e) {
                log.error("请求异常：" + e);
            }
        }
    }

    public static Object doDelete(String url, Map<String, String> headersMap) throws Exception {
        CloseableHttpClient httpClient = getHttpClient(url);
        HttpDelete httpDelete;
        try {
            httpDelete = new HttpDelete(url);
            addConfig(httpDelete);
            addHeaderToRequest(headersMap, httpDelete);
            CloseableHttpResponse httpResponse = httpClient.execute(httpDelete);
            return dealHttpResponse(httpResponse);
        } catch (Exception ex) {
            log.error("请求异常：" + ex.getMessage());
            return null;
        } finally {
            try {
                if (null != httpClient) {
                    httpClient.close();
                }

            } catch (IOException e) {
                log.error("请求异常：" + e);
            }
        }
    }

    public static Object doPatch(String url, Map<String, String> paramsMap, Map<String, String> headersMap, String body) throws Exception {
        CloseableHttpClient httpClient = getHttpClient(url);
        HttpPatch httpPatch;
        try {
            httpPatch = new HttpPatch(url);
            addConfig(httpPatch);
            addHeaderToRequest(headersMap, httpPatch);
            addBody(httpPatch, paramsMap, headersMap, body);
            CloseableHttpResponse httpResponse = httpClient.execute(httpPatch);
            return dealHttpResponse(httpResponse);
        } catch (Exception ex) {
            log.error("请求异常：" + ex.getMessage());
            return null;
        } finally {
            try {
                if (null != httpClient) {
                    httpClient.close();
                }

            } catch (IOException e) {
                log.error("请求异常：" + e);
            }
        }
    }

    private static void addBody(HttpEntityEnclosingRequestBase http, Map<String, String> paramsMap, Map<String, String> headersMap, String body) {
        try {
            if (null == paramsMap)
                paramsMap = new HashMap<>();

            if (headersMap != null && headersMap.get("Content-Type") != null && headersMap.get("Content-Type").contains("x-www-form-urlencoded")) {
                List<NameValuePair> params = new ArrayList<>();
                for (String s : paramsMap.keySet()) {
                    params.add(new BasicNameValuePair(s, paramsMap.get(s)));
                }
                http.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            } else {
                if (null != body) {
                    StringEntity entity = new StringEntity(body, "utf-8");
                    entity.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    entity.setContentEncoding("utf-8");
                    http.setEntity(entity);
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("addBody异常：" + e);
        }
    }

    private static void addConfig(HttpRequestBase http) {
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
        http.setConfig(requestConfig);
    }

    private static void addHeaderToRequest(Map<String, String> headersMap, HttpRequestBase request) {
        if (headersMap != null && headersMap.size() > 0) {
            if (!headersMap.containsKey("User-Agent")) {
                headersMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
            }
            //删除掉Content-Length header
            headersMap.remove("Content-Length");

            Header[] headers = new Header[headersMap.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headers[i] = new BasicHeader(entry.getKey(), entry.getValue());
                i++;
            }
            request.setHeaders(headers);
        } else {
            request.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36"));
        }
    }

    /**
     * 处理响应结果
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static Object dealHttpResponse(HttpResponse response) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, String>> headerArr = new ArrayList<>();
        for (Header header : response.getAllHeaders()) {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("name", header.getName());
            headerMap.put("value", header.getValue());
            headerArr.add(headerMap);
        }
        resultMap.put("headers", headerArr);
        String result;
        if (response.getFirstHeader("Content-Encoding") != null && response.getFirstHeader("Content-Encoding").getValue().contains("gzip")) {
            result = dealGzipResponse(response);
        } else {

            result = response.getEntity() == null ? null : EntityUtils.toString(response.getEntity(), "utf-8");
        }

        resultMap.put("response", result);
        return resultMap;
    }

    private static String dealGzipResponse(HttpResponse response) {
        try {
            ByteArrayInputStream byteBuf = new ByteArrayInputStream(EntityUtils.toByteArray(response.getEntity()));
            GZIPInputStream gzip = new GZIPInputStream(byteBuf);
            byte[] decompressedData = IOUtils.toByteArray(gzip);
            return (new String(decompressedData, "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static CloseableHttpClient getHttpClient(String url) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        if (url.startsWith("https://")) {
            httpClient = new SSLClient();
        }
        return httpClient;
    }
}
