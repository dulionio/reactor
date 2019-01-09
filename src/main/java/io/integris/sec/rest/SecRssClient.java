package io.integris.sec.rest;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.integris.sec.message.bean.Message;

public class SecRssClient {

    private static final URI RSS = URI.create("https://www.sec.gov/Archives/edgar/monthly/xbrlrss-2005-04.xml");

    private final CloseableHttpClient _client;

    public SecRssClient() {
        _client = HttpClientBuilder.create()
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setConnectTimeout(10 * 1000)
                    .setSocketTimeout(120 * 1000)
                    .build())
            .build();
    }

    public Publisher<Message> publish(Message source) {
        return sink -> publish(source, sink);

    }

    private void publish(Message source, Subscriber<? super Message> sink) {
        HttpGet request = new HttpGet(RSS);
        request.addHeader("Accept", "application/xml");
        try (CloseableHttpResponse response = _client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                sink.onNext(new Message(source).setInputStream(response.getEntity().getContent()));
            } else {
                sink.onError(new HttpResponseException(statusCode, response.getStatusLine().getReasonPhrase()));
            }
            sink.onComplete();
        } catch (IOException ex) {
            sink.onError(ex);
        }
    }
}
