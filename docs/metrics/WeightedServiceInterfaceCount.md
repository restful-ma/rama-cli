# Weighted Service Interface Count (WSIC)
The `Weighted Service Interface Count` metric (WSIC) represents the weighted number of exposed operations in a service interface. The default weight is set to 1. Alternate weighting methods, which need to be validated empirically, can take into consideration the number and the complexity of data types of parameters in each interface. In the default case, WSIC simply returns a count of the number of exposed methods. 

The greater the number of defined interfaces per service within a SOA solution the more complex a service becomes due to the following factors. 
- The amount of work required to specify, construct and test every interface on the service increases. 
- The amount of monitoring required to ensure that service level agreements (SLAs) are met increases with every invocation of an interface.
- With the increase in complexity of individual interfaces of the data structures for a given service, performance and problem determination concerns may become a primary issue. Performance and root cause issues are hard to predict and diagnose. 

## Source
Hirzalla, Mamoun, Jane Cleland-Huang, and Ali Arsanjani. "A metrics suite for evaluating flexibility and complexity in service oriented architectures." International Conference on Service-Oriented Computing. Springer, Berlin, Heidelberg, 2008.