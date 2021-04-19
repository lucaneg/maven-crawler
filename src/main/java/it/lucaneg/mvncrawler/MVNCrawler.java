package it.lucaneg.mvncrawler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import it.lucaneg.mvncrawler.Crawler.CrawlerBuilder;

public class MVNCrawler {

	private static final Option WORKDIR;
	private static final Option PROXY_HOST;
	private static final Option PROXY_PORT;
	private static final Option PROXY_USER;
	private static final Option PROXY_PASSWORD;
	private static final Option LIMIT;

	static {
		WORKDIR = Option.builder("D").longOpt("workdir").hasArg()
				.desc("the directory where the downloaded jars will be stored").required().build();
		PROXY_HOST = Option.builder("H").longOpt("proxy-host").hasArg().desc("url of proxy host").build();
		PROXY_PORT = Option.builder("P").longOpt("proxy-port").hasArg().desc("port number of proxy host")
				.build();
		PROXY_USER = Option.builder("u").longOpt("proxy-user").hasArg()
				.desc("user name of proxy-authentication").build();
		PROXY_PASSWORD = Option.builder("p").longOpt("proxy-pass").hasArg()
				.desc("password of proxy-authentication").build();
		LIMIT = Option.builder("l").longOpt("limit").hasArg()
				.desc("maximum number of jars to download, default is 500").build();
	}

	private static CommandLine cmdLine;

	public static void main(String[] args) {
		Options options = buildOptions();
		try {
			cmdLine = new DefaultParser().parse(options, args);

			CrawlerBuilder builder = new CrawlerBuilder();

			builder.withWorkdir(cmdLine.getOptionValue(WORKDIR.getOpt()));

			if (cmdLine.hasOption(PROXY_HOST.getOpt()))
				builder.withProxy(
						cmdLine.getOptionValue(PROXY_HOST.getOpt()),
						parseIntOr(cmdLine.getOptionValue(PROXY_PORT.getOpt()), 8080),
						cmdLine.getOptionValue(PROXY_USER.getOpt()),
						cmdLine.getOptionValue(PROXY_PASSWORD.getOpt()));
			else
				builder.withoutProxy();

			if (cmdLine.hasOption(LIMIT.getOpt()))
				builder.withLimit(parseIntOr(cmdLine.getOptionValue(LIMIT.getOpt()), 500));

			builder.build().doWork();
		} catch (ParseException e) {
			printUsage(options);
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println("Exception during execution of the crawler:\n" + e.getMessage() + "\n");
			e.printStackTrace(System.err);
		}
	}

	private static int parseIntOr(String s, int defaultValue) {
		if (s == null)
			return defaultValue;

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			System.out.println("WARNING: cannot parse '" + s + "' as an integer, defaulting to " + defaultValue);
			return defaultValue;
		}
	}

	private static void printUsage(Options options) {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp(MVNCrawler.class.getSimpleName(), options);
	}

	private static Options buildOptions() {
		Options result = new Options();
		result.addOption(WORKDIR);

		result.addOption(PROXY_HOST);
		result.addOption(PROXY_PORT);
		result.addOption(PROXY_USER);
		result.addOption(PROXY_PASSWORD);

		result.addOption(LIMIT);
		return result;
	}
}
