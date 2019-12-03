package restful.api.metric.analyzer.cli;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import restful.api.metric.analyzer.cli.parser.ParserFactory;
import restful.api.metric.analyzer.cli.services.ApplicationService;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		String strArgs = Arrays.stream(args).collect(Collectors.joining("|"));
		logger.info("Application started with arguments: {}", strArgs);

		Options options = new Options();

		Option uriInput = new Option("uri", true, "input URI path");
		uriInput.setRequired(false);
		options.addOption(uriInput);

		Option fileInput = new Option("file", true, "input local file path");
		fileInput.setRequired(false);
		options.addOption(fileInput);

		Option formatInput = new Option("format", true, "input format, allowed arguments are " +  ParserFactory.getAllowedParserTypes());
		formatInput.setRequired(true);
		options.addOption(formatInput);

		// at least one of the two input options is required for the program
		OptionGroup inputOptions = new OptionGroup();
		inputOptions.addOption(uriInput);
		inputOptions.addOption(fileInput);
		inputOptions.setRequired(true);
		options.addOptionGroup(inputOptions);

		Option jsonOutput = new Option("json", true, "json output file path");
		jsonOutput.setRequired(false);
		options.addOption(jsonOutput);

		Option pdfOutput = new Option("pdf", true, "pdf output file path");
		pdfOutput.setRequired(false);
		options.addOption(pdfOutput);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
			return;
		}

		String uriPath = cmd.getOptionValue("uri");
		String filePath = cmd.getOptionValue("file");
		String format = cmd.getOptionValue("format");
		String jsonOutputPath = cmd.getOptionValue("json");
		String pdfOutputPath = cmd.getOptionValue("pdf");

		ApplicationService applicationService = new ApplicationService();

		if (uriPath != null) {
			try {
				applicationService.loadURIPath(uriPath, format);
			} catch (java.text.ParseException e) {
				logger.error("Failed to load or parse file from uri", e);
			}
		}

		if (filePath != null) {
			try {
				applicationService.loadLocalFilePath(filePath, format);
			} catch (java.text.ParseException e) {
				logger.error("Failed to load or parse file", e);
			}
		}

		if (uriPath != null && filePath == null || uriPath == null && filePath != null) {
			try {
				applicationService.createRestfulService(false);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException |
					SecurityException | InstantiationException e) {
				logger.error("Failed to create API service", e);
			}
			applicationService.commandLineLogger();
			if (jsonOutputPath != null) {
				applicationService.exportAsJson(jsonOutputPath);
			}
			if (pdfOutputPath != null) {
				applicationService.exportAsPDF(pdfOutputPath);
			}
		}
	}
}