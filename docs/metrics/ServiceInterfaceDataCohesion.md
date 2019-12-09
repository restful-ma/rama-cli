# Service Interface Data Cohesion (SIDC)
The ServiceInterfaceDataCohesion metric (SIDC) quantifies the cohesion of a given service _S_ based on the similarity of data types present in the operations _SO(SI<sub>S</sub>)_ of its service interface _SI<sub>S</sub>_. In particular, SIDC evaluates if service operations have

- common input parameter types
- common return types

A service is deemed to be data cohesive when all possible pairs of service operations have at least one common parameter and return type. The values of SIDC range from `0` to `1`, with the value `1` representing the strongest possible data cohesion.

## Formal definition

_SIDC(S) = (Common (Params (SO(SI<sub>S</sub>))) + Common (returnTypes (SO(SI<sub>S</sub>)))) / (Total (SO(SI<sub>S</sub>)) \* 2)_

- _Common (Params (SO(SI<sub>S</sub>)))_ is the function that calculates the number of service operation pairs that have at least one input parameter type in common.
- _Common (returnTypes (SO(SI<sub>S</sub>)))_ is the function that calculates the number of service operation pairs that have the same return type.
- _Total (SO(SI<sub>S</sub>))_ is the function that returns the number of all possible combinations of operation pairs for the service interface SI<sub>S</sub>. It can be calculated as follows, where _n_ denotes the number of operations in the interface: _Total (SO(SI<sub>S</sub>)) = ((n - 1) \* n) / 2_

## Source

M. Perepletchikov, "Software Design Metrics for Predicting Maintainability of Service-Oriented Software", PhD thesis, College of Science, Engineering and Health, RMIT University, Melbourne, Australia, 2009.
