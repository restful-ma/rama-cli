package restful.api.metric.analyzer.cli.parser;

public enum ParserType {
	OPENAPI, RAML, WADL;

	public Parser getParser() {
		switch (this) {
		case OPENAPI:
			return new OpenApi3Parser();
		case RAML:
			return new RAMLParser();
		case WADL:
			return new WadlParser();
		default:
			return null;
		}

	}
}
