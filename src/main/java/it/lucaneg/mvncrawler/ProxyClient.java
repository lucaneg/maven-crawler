package it.lucaneg.mvncrawler;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ProxyClient extends BaseClient {

	private final String proxyHost;
	private final String user;
	private final String password;
	private final int proxyPort;

	public ProxyClient(String proxyUrl, int proxyPort, String user, String password) {
		this.proxyHost = proxyUrl;
		this.user = user;
		this.password = password;
		this.proxyPort = proxyPort;
	}

	@Override
	protected CloseableHttpClient getHttpClient() {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(user, password));

		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		return httpclient;
	}

	@Override
	protected HttpResponse getResponse(String link, CloseableHttpClient httpclient)
			throws IOException {
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);

		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

		HttpGet httpget = new HttpGet(link);
		httpget.setConfig(config);

		HttpResponse response = httpclient.execute(httpget);
		return response;
	}
}
