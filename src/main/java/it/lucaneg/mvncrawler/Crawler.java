package it.lucaneg.mvncrawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crawler {

	private static final String[] CANDIDATES = {
			// 'org' has the most jars hence it is listed 3 times to have 3
			// times the chance of hitting
			"repo1.maven.org/maven2/org/",
			"repo1.maven.org/maven2/org/",
			"repo1.maven.org/maven2/org/",
			// 'com', 'io' and 'net' are pretty big as well
			"repo1.maven.org/maven2/com/",
			"repo1.maven.org/maven2/io/",
			"repo1.maven.org/maven2/net/",
			// this the rest
			"repo1.maven.org/maven2/"
	};

	private static final int RETRIES = 5;

	private static final Random RANDOM = new Random();

	private final Client siteReader;
	private final String workdir;
	private final int limit;

	private Crawler(Client siteReader, String workdir, int limit) {
		this.siteReader = siteReader;
		this.workdir = workdir;
		this.limit = limit;
	}

	public void doWork() {
		int count = 0;

		File destFolder = new File(workdir);
		if (!destFolder.exists())
			destFolder.mkdirs();

		try {
			while (count++ < limit) {
				String jarURL = crawl();
				String localJarFile = download(jarURL);
				System.out.println("--- downloaded: " + localJarFile);
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println("Downloaded " + (count - 1) + " jars");
	}

	private String crawl() throws IOException {
		String startUrl = pickURL();
		for (int i = 0; i < RETRIES; ++i) {
			String result = randomJar(startUrl);
			if (result != null)
				return result;
		}

		throw new IOException("Max retries (" + RETRIES + ") reached on " + startUrl);
	}

	private String pickURL() {
		return "https://" + CANDIDATES[RANDOM.nextInt(CANDIDATES.length)];
	}

	private String randomJar(String baseUrl) {
		List<String> validLinks = getValidLinks(baseUrl);

		int size = validLinks.size();
		if (size == 0)
			return null;

		int index = RANDOM.nextInt(size);
		String newLink = validLinks.get(index);

		if (newLink.endsWith(".jar"))
			return newLink;
		else
			return randomJar(newLink);
	}

	// a link is valid if it is a directory link, but not "../", or a jar link
	private List<String> getValidLinks(String baseUrl) {
		List<String> result = new ArrayList<>();

		List<String> lines;
		try {
			lines = read(baseUrl);
		} catch (IOException e) {
			return result;
		}

		String linkStart = "<a href=\"";

		for (String line : lines) {
			int startIndex = line.indexOf(linkStart);
			if (startIndex >= 0) {
				int endIndex = line.indexOf('\"', startIndex + linkStart.length());
				if (endIndex >= 0) {
					String link = line.substring(startIndex + linkStart.length(), endIndex);
					if (link.equals("../"))
						continue;

					if (link.endsWith("-javadoc.jar") || link.endsWith("-sources.jar"))
						continue;

					if (link.endsWith("/") || link.endsWith(".jar"))
						result.add(baseUrl + link);
				}
			}
		}

		return result;
	}

	private List<String> read(String baseUrl) throws IOException {
		return siteReader.read(baseUrl);
	}

	private String download(String jarURL) throws IOException {
		String[] parts = jarURL.split("/");
		String fileName = workdir + parts[parts.length - 1];
		if (!new File(fileName).exists())
			siteReader.download(jarURL, fileName);
		return fileName;
	}

	public static class CrawlerBuilder {

		private Client siteReader = new DefaultClient();
		private String workdir = "downloadedJars";
		private int limit = 500;

		public CrawlerBuilder() {
		}

		public CrawlerBuilder withWorkdir(String path) {
			if (path.endsWith(File.separator))
				workdir = path;
			else
				workdir = path + File.separator;
			return this;
		}

		public CrawlerBuilder withProxy(String host, int port, String user, String password) {
			siteReader = new ProxyClient(host, port, user, password);
			return this;
		}

		public CrawlerBuilder withoutProxy() {
			siteReader = new DefaultClient();
			return this;
		}

		public CrawlerBuilder withLimit(int limit) {
			this.limit = limit;
			return this;
		}

		public Crawler build() {
			return new Crawler(siteReader, workdir, limit);
		}
	}
}
