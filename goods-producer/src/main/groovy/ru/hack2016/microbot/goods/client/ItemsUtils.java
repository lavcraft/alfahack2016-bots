package ru.hack2016.microbot.goods.client;

import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * @author tolkv
 * @since 05/12/15
 */
@Slf4j
public class ItemsUtils {
  public static void send(String items) throws IOException, KeyManagementException, NoSuchAlgorithmException {

    final TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
          }

          @Override
          public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
          }

          @Override
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }
        }
    };

    final SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setSslSocketFactory(sslSocketFactory);
    okHttpClient.setHostnameVerifier((hostname, session) -> true);
    okHttpClient.setCertificatePinner(CertificatePinner.DEFAULT);

    Response execute = okHttpClient.newCall(new Request.Builder()
        .url("https://fridge.octoberry.ru/query?q=" + URLEncoder.encode(items, "UTF-8"))
        .get()
        .build()).execute();
    log.info("execute: {}", execute.body().string());
  }
}
