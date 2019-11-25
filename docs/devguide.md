# RESTful API Metric Analyzer CLI
The RESTful API Metric Analyzer CLI (RAMA) is a tool that can evaluate the maintainability of restful APIs based on their API description.
Currently RAMA can evaluate APIs that are written in the following RESTful API Description Languages:
* OpenAPI V3 https://github.com/OAI/OpenAPI-Specification
* RAML 1.0 https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md/
* WADL https://www.w3.org/Submission/wadl/

RAMA is developed as a s Java based command-line tool.

RAMA expects a specification file and returna a maintainability report as JSON or PDF.
<br> The maintainability report consists of general information and  maintainability metrics that are described in [Metrics](###Metrics) 
<br>Example
```bash
# Run with public URI:
java -jar ./target/openapi-evaluation-cli-0.0.5-jar-with-dependencies.jar -uri http://url-to-swagger-file.com -format openapi
```
produces following output



# How to use RAMA CLI
## How to build RAMA CLI
## How to run  
### Command-line Options

| Option        | Description   | Required|
| :-------------|:--------------|:--------|
| -uri $URI_PATH    | The option -uri is used, if the specification file is located in the web. | YES* 
| -file $FILE_PATH  |  The option -file is used, if the OpenAPI specification is located on the local computer. | YES*
| -format $FORMAT  |  The option -format is used to decide which specifiaction file format should be analysed. Allowed $Format are openapi, raml, wadl | YES
| -pdf $OUTPUT_PATH |  The option -pdf is used, if the user wants an PDF output. A path must be specified behind which the file is saved. | NO** 
| -json $OUTPUT_PATH | The option -json is used, if the user wants an JSON output. A path must be specified behind which the file is saved. | NO**

*Either a file or a uri has to be specified 
<br>**If no additional output was specified the report will be only displayed on the console. 

Example 
```bash
# Run with public URI:
java -jar ./target/openapi-evaluation-cli-0.0.5-jar-with-dependencies.jar -uri http://url-to-swagger-file.com -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run with local file:
java -jar ./target/openapi-evaluation-cli-0.0.5-jar-with-dependencies.jar -file path/to/file.yaml -pdf path/to/file.pdf -json path/to/file.json -format openapi

# Run petstore example with public URL:
java -jar ./target/openapi-evaluation-cli-0.0.5-jar-with-dependencies.jar -uri https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml -format openapi

# Run customer-srv example with local URL:
java -jar ./target/openapi-evaluation-cli-0.0.5-jar-with-dependencies.jar -file ./src/test/resources/CustomerSrv-openapi.yaml -format openapi

```


# How to Contribute
## RAMA CLI General Architecture
### UML Sequence Diagram
Description of the tools behaviour if it is started via command-line.
![Sequence Diagram](sequence.png)
#### Main
Entry point of the tool. Command-line [options](###Command-line Options) are detected and passed to the ApplicationService.
#### ApplicationService
Consist the logic to create:
* Service
* Evaluations
* Measurement
#### Parsers
All Parsers 
#### Internal API Model
#### Metrics
## How to add or modify  parsers ?
## How to add or modify metrics ?
## How to add or modify tests  ?

# Other Tools