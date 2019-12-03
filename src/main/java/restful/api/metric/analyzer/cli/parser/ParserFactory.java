package restful.api.metric.analyzer.cli.parser;

import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserFactory {
	private static final Logger logger = LoggerFactory.getLogger(ParserFactory.class);

	private ParserFactory() {
		throw new IllegalStateException("Utility class");
	}

	public static Parser getParser(String format) {
		try {
			ParserType type = Enum.valueOf(ParserType.class, format.toUpperCase());
			return type.getParser();
		} catch (Exception e) {
			logger.info("Invalid format: {}. Allowed formats are: {}", format, ParserFactory.getAllowedParserTypes());
		}

		return null;
	}

	public static String getAllowedParserTypes() {
		StringJoiner sj = new StringJoiner(", ");
		for (ParserType type : ParserType.values()) {
			sj.add(type.name());
		}

		return sj.toString();
	}

}
