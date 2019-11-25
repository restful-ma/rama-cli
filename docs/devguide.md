# Developement Guide
## RAMA CLI General Architecture
### UML Sequence Diagram
Description of the tools behaviour if it is started via command-line.
![Sequence Diagram](sequence.png)

#### Main
Entry point of the tool. Command-line [options](###Command-line Options) are detected and passed to the ApplicationService.
#### ApplicationService

the ApplicationService is responsible for creating:
* RestfulApiSystem 
* RestfulApiService
* Evaluations
* Measurement

| Class        | Responsibility   | 
| :-------------|:--------------|
|RestfulApiSystem|One system can consist of multiple APIs. This Entity is used to store the system name and a list of `RestfulApiServices`. For the usage in the command-line this is not used. Can be used in combination of the [other tools](#Other%20Tools).   |
|RestfulApiService|Represents a single specification file. Can store meta information like name or the file. Also stores a list of `Evaluations`. This entity is later used by the `RestfulServiceMapper` to generate PDF or JSON files. |
|Evaluations|Contains multiple `Measurements` and also other metadata|
|Measurement|Represents the measurement of a single metric|

![Domain Diagram](domain-model.png)



#### Parsers
All Parsers inherit from the `Parser.java` class.
The Parser are responsible for transforming specification files from each format to our [Internal API Model](####Internal%20API%20Model)
#### Internal API Model
TODO 

## How to add or modify  parsers ?
## How to add or modify metrics ?
## How to add or modify tests  ?
# Metrics
The tool currently has 10 metrics that are described as follows:
* [Argument per Operation (APO)](metrics/ArgumentsPerOperation.md)
* [Average Path Length (APL)](metrics/AveragePathLength.md)
* [Biggest Root Coverage (BRC)](metrics/BiggestrootCoverage.md)
* [DataWeight (DW)](metrics/DataWeight.md)
* [Longest Path (LP)](metrics/LongestPath.md)
* [Number Of Root Resources (NOR)](metrics/NumberOfRoots)
* [Service Interface Data Cohesion (SIDC)](metrics/ServiceInterfaceDataCohesion.md)
* [Weighted Service Interface Count (WSIC)](metrics/WeightedServiceInterfaceCount.md)

# Other Tools