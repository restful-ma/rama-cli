# RAMA CLI

[![Actions Status](https://action-badges.now.sh/restful-ma/rama-cli)](https://github.com/restful-ma/rama-cli/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=restful-ma_rama-cli&metric=alert_status)](https://sonarcloud.io/dashboard?id=restful-ma_rama-cli)
[![SonarQube Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=restful-ma_rama-cli&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=restful-ma_rama-cli)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=restful-ma_rama-cli&metric=ncloc)](https://sonarcloud.io/dashboard?id=restful-ma_rama-cli)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=restful-ma_rama-cli&metric=coverage)](https://sonarcloud.io/dashboard?id=restful-ma_rama-cli)

The RAMA (RESTful API Metric Analyzer) CLI is a tool that can evaluate the maintainability of RESTful APIs via several structural metrics. These metrics are calculated based on machine-readable API documentation.
Currently, RAMA can parse the following RESTful API description languages:
* OpenAPI V3 https://github.com/OAI/OpenAPI-Specification
* RAML 1.0 https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md/
* WADL https://www.w3.org/Submission/wadl/

RAMA has been developed in several study and research projects at the [Software Engineering Group](https://www.iste.uni-stuttgart.de/se) of the University of Stuttgart as a Java command-line tool. It takes the path to a specification file as input and returns a maintainability report with service interface metrics as JSON or PDF.
The maintainability report consists of maintainability metrics that are described [here](docs/metrics/README.md).

Example:
```bash
# Run with local OpenAPI file
java -jar ./target/rama-cli-0.1.2.jar -file ./src/test/resources/OA3OldFiles/api-with-examples.yaml -format openapi
```

This produces the following output:
```bash
$ java -jar ./target/rama-cli-0.1.2.jar -file ./src/test/resources/OA3OldFiles/api-with-examples.yaml -format openapi
Dec 04, 2019 11:20:43 AM restful.api.metric.analyzer.cli.Main main
INFO: Application started with arguments: -file|./src/test/resources/OA3OldFiles/api-with-examples.yaml|-format|openapi
Dec 04, 2019 11:20:44 AM org.reflections.Reflections scan
INFO: Reflections took 31 ms to scan 1 urls, producing 1 keys and 10 values
Dec 04, 2019 11:20:44 AM restful.api.metric.analyzer.cli.services.ApplicationService commandLineLogger
INFO: {
  "apiTitle": "Simple API overview",
  "apiVersion": "2.0.0",
  "apiFileName": "api-with-examples.yaml",
  "apiFormat": "OPENAPI",
  "measurementDate": "2019/12/04",
  "measurement": [
    {
      "metricName": "DistinctMessageRatio",
      "metricValue": 0.25
    },
    {
      "metricName": "LackOfMessageLevelCohesion",
      "metricValue": 1.0
    },
    {
      "metricName": "ServiceInterfaceDataCohesion",
      "metricValue": 0.5,
      "additionalInformation": "Metric value range: [0-1]; Set of pairwise operations with at least one common parameter: []; Set of pairwise operations with common return type: [[listVersionsv2, getVersionDetailsv2]]; Number of operations: 2.0"
    },
    {
      "metricName": "AveragePathLength",
      "metricValue": 1.0
    },
    {
      "metricName": "WeightedServiceInterfaceCount",
      "metricValue": 2.0,
      "additionalInformation": "Number of different operations: 2"
    },
    {
      "metricName": "BiggestRootCoverage",
      "metricValue": 0.5,
      "additionalInformation": "Biggest root coverage: /emptyRootName with 1 operation(s) from overall 2 operation(s)"
    },
    {
      "metricName": "DataWeight",
      "metricValue": 0.0
    },
    {
      "metricName": "ArgumentsPerOperation",
      "metricValue": 0.0
    },
    {
      "metricName": "LongestPath",
      "metricValue": 1.0,
      "additionalInformation": "Longest path: /, Length: 1"
    },
    {
      "metricName": "NumberOfRootPaths",
      "metricValue": 2.0,
      "additionalInformation": "all root paths: /emptyRootName,/v2"
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
| Option               | Description                                                                                                     | Required |
| :------------------- | :-------------------------------------------------------------------------------------------------------------- | :------- |
| `-uri $URI_PATH`     | Used to analyze a specification file on the web (the URI has to reference the file directly).                   | YES*     |
| `-file $FILE_PATH`   | Used to analyze a local specification file.                                                                     | YES*     |
| `-format $FORMAT`    | Used to indicate which specification file format to analyze. Supported values are `openapi`, `raml`, or `wadl`. | YES      |
| `-pdf $OUTPUT_PATH`  | Used to generate a PDF file report. A path where the file will be saved must be specified.                      | NO**     |
| `-json $OUTPUT_PATH` | Used to generate a JSON file report. A path where the file will be saved must be specified.                     | NO**     |

*Either a file or a URI has to be specified as input.<br>
**If no additional output was specified, the report will only be outputted to the console.

```bash
# Run with public URI:
java -jar ./target/rama-cli-0.1.2.jar -uri http://url-to-swagger-file.com -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run with local file:
java -jar ./target/rama-cli-0.1.2.jar -file path/to/file.yaml -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run petstore example with public URL:
java -jar ./target/rama-cli-0.1.2.jar -uri https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml -format openapi

# Run customer-srv example with local URL:
java -jar ./target/rama-cli-0.1.2.jar -file ./src/test/resources/OA3OldFiles/CustomerSrv-openapi.yaml -format openapi
```

## How to Contribute
Descriptions of the architecture, components, and metrics can be found in our [developer guide](docs/README.md).
