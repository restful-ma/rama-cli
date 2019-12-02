# RAMA CLI
The RAMA (RESTful API Metric Analyzer) CLI is a tool that can evaluate the maintainability of RESTful APIs via several structural metrics. These metrics are calculated based on machine-readable API documentation.
Currently, RAMA can parse the following RESTful API description languages:
* OpenAPI V3 https://github.com/OAI/OpenAPI-Specification
* RAML 1.0 https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md/
* WADL https://www.w3.org/Submission/wadl/

RAMA is developed as a Java-based command-line tool.

RAMA takes the path to a specification file as input and returns a maintainability report with metrics as JSON or PDF.
The maintainability report consists of general information and  maintainability metrics that are described [here](docs/devguide.md#Metrics).

Example:
```bash
# Run with public URI:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -uri http://url-to-swagger-file.com -format openapi
```
produces following output

ADD EXAMPLE !!!



## General Usage Instructions

```bash
# Build and package executable:
mvn clean install

# Build and package executable without running tests:
mvn clean install -DskipTests

# Execute tests only:
mvn clean test
```

TODO: examples for running the tool; result example?

## Command-Line Options

| Option        | Description   | Required|
| :-------------|:--------------|:--------|
| -uri $URI_PATH    | The option -uri is used, if the specification file is located in the web (the uri has to reference directly to the file). | YES* 
| -file $FILE_PATH  |  The option -file is used, if the OpenAPI specification is located on the local computer. | YES*
| -format $FORMAT  |  The option -format is used to decide which specifiaction file format should be analysed. Allowed $Format are openapi, raml, wadl | YES
| -pdf $OUTPUT_PATH |  The option -pdf is used, if the user wants an PDF output. A path must be specified behind which the file is saved. | NO** 
| -json $OUTPUT_PATH | The option -json is used, if the user wants an JSON output. A path must be specified behind which the file is saved. | NO**

*Either a file or a URI has to be specified<br>
**If no additional output was specified the report will be only displayed on the console. 

Example TODO TEST all paths

```bash
# Run with public URI:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -uri http://url-to-swagger-file.com -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run with local file:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -file path/to/file.yaml -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run petstore example with public URL:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -uri https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml -format openapi

# Run customer-srv example with local URL:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -file ./src/test/resources/CustomerSrv-openapi.yaml -format openapi
```


## How to Contribute
Descriptions of the architecture, components, and metrics can be found in our [devguide](docs/devguide.md).
