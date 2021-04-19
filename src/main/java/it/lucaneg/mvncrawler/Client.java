package it.lucaneg.mvncrawler;

import java.io.IOException;
import java.util.List;

public interface Client {

	public List<String> read(String link) throws IOException;

	public void download(String link, String targetPath) throws IOException;
}