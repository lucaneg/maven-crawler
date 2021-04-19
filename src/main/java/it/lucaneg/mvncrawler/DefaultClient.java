package it.lucaneg.mvncrawler;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DefaultClient extends BaseClient {

	@Override
	protected CloseableHttpClient getHttpClient() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		return httpclient;
	}

	@Override
	protected HttpResponse getResponse(String link, CloseableHttpClient httpclient)
			throws IOException {
		RequestConfig config = RequestConfig.custom().build();

		HttpGet httpget = new HttpGet(link);
		httpget.setConfig(config);

		HttpResponse response = httpclient.execute(httpget);
		return response;
	}
}
