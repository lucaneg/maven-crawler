package it.lucaneg.mvncrawler;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public abstract class BaseClient implements Client {

	@Override
	public List<String> read(String link) throws IOException {
		CloseableHttpClient httpclient = getHttpClient();

		try {
			HttpResponse response = getResponse(link, httpclient);
			return read(response);
		} finally {
			httpclient.close();
		}
	}

	private List<String> read(HttpResponse response) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		List<String> result = new ArrayList<String>();
		String line = "";
		while ((line = reader.readLine()) != null) 
			result.add(line);

		return result;
	}

	@Override
	public void download(String link, String targetPath) throws IOException {
		CloseableHttpClient httpclient = getHttpClient();

		try {
			HttpResponse response = getResponse(link, httpclient);

			try (InputStream data = response.getEntity().getContent();
					OutputStream output = new FileOutputStream(targetPath)) {
				data.transferTo(output);
			}

		} finally {
			httpclient.close();
		}
	}

	protected abstract HttpResponse getResponse(String link, CloseableHttpClient httpclient)
			throws IOException;

	protected abstract CloseableHttpClient getHttpClient();
}
