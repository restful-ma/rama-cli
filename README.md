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
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -file ./src/test/resources/OA3OldFiles/api-with-examples.yaml -format openapi
```
produces following output

```
$ java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -file ./src/test/resources/OA3OldFiles/api-with-examples.yaml -format openapi                              Dez. 03, 2019 5:58:10 NACHM. restful.api.metric.analyzer.cli.Main main
INFO: Application started with arguments: -file|./src/test/resources/OA3OldFiles/api-with-examples.yaml|-format|openapi
Dez. 03, 2019 5:58:13 NACHM. org.reflections.Reflections scan
INFO: Reflections took 328 ms to scan 1 urls, producing 1 keys and 10 values
Dez. 03, 2019 5:58:13 NACHM. restful.api.metric.analyzer.cli.services.ApplicationService commandLineLogger
INFO: {
  "apiTitle": "Simple API overview",
  "apiVersion": "2.0.0",
  "apiFileName": "api-with-examples.yaml",
  "apiFormat": "OPENAPI",
  "measurementDate": "2019/12/03",
  "measurement": [
    {
      "metricName": "DataWeight",
      "metricValue": 0.0
    },
    {
      "metricName": "BiggestRootCoverage",
      "metricValue": 0.5,
      "additionalInformation": "Biggest root coverage: /emptyRootName with 1 operation(s) from overall 2 operation(s)"
    },
    {
      "metricName": "WeightedServiceInterfaceCount",
      "metricValue": 2.0,
      "additionalInformation": "Number of different operations: 2"
    },
    {
      "metricName": "LongestPath",
      "metricValue": 1.0,
      "additionalInformation": "Longest path: /, Length: 1"
    },
    {
      "metricName": "LackOfMessageLevelCohesion",
      "metricValue": 1.0
    },
    {
      "metricName": "DistinctMessageRatio",
      "metricValue": 0.25
    },
    {
      "metricName": "ServiceInterfaceDataCohesion",
      "metricValue": 0.5,
      "additionalInformation": "Metric value range: [0-1]; Set of pairwise operations with at least one common parameter: []; Set of pairwise operations with common return type: [[listVersionsv2, getVersionDetailsv2]]; Number of operations: 2.0"
    },
    {
      "metricName": "ArgumentsPerOperation",
      "metricValue": 0.0
    },
    {
      "metricName": "NumberOfRootPaths",
      "metricValue": 2.0,
      "additionalInformation": "all root paths: /emptyRootName,/v2"
    },
    {
      "metricName": "AveragePathLength",
      "metricValue": 1.0
    }
  ]
}

```



## General Usage Instructions

```bash
# Build and package executable:
mvn clean install

# Build and package executable without running tests:
mvn clean install -DskipTests

# Execute tests only:
mvn clean test
```

## Command-Line Options

| Option        | Description   | Required|
| :-------------|:--------------|:--------|
| -uri $URI_PATH    | The option -uri is used, if the specification file is located in the web (the uri has to reference directly to the file). | YES* 
| -file $FILE_PATH  |  The option -file is used, if the OpenAPI specification is located on the local computer. | YES*
| -format $FORMAT  |  The option -format is used to decide which specification file format should be analysed. Allowed $Format are openapi, raml, wadl | YES
| -pdf $OUTPUT_PATH |  The option -pdf is used, if the user wants an PDF output. A path must be specified behind which the file is saved. | NO** 
| -json $OUTPUT_PATH | The option -json is used, if the user wants an JSON output. A path must be specified behind which the file is saved. | NO**

*Either a file or a URI has to be specified<br>
**If no additional output was specified the report will be only displayed on the console. 

```bash
# Run with public URI:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -uri http://url-to-swagger-file.com -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run with local file:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -file path/to/file.yaml -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run petstore example with public URL:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -uri https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml -format openapi

# Run customer-srv example with local URL:
java -jar ./target/restful-api-metric-analyzer-cli-0.0.7-jar-with-dependencies.jar -file ./src/test/resources/OA3OldFiles/CustomerSrv-openapi.yaml -format openapi
```


## How to Contribute
Descriptions of the architecture, components, and metrics can be found in our [devguide](docs/devguide.md).
