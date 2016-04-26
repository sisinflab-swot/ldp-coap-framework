ldp-coap-core
===================

Basic framework implementation including the proposed LDP-CoAP mapping.

Implementation
-------------
_ldp-coap-core_ includes two simple test servers:

- _CoAPLDPTestServer_, exposing all type of LDP resources: RDFSource, NonRDFSource, Basic Container, Direct Container and Indirect Container;
- _CoAPLDPTestSuiteServer_, exposing basic resources used to test LDP-CoAP implementation along with the [W3C Test Suite for LDP](http://w3c.github.io/ldp-testsuite/).

Also two resource handlers were implemented:
- _CPUHandler_, exposing the percentage of system CPU load as LDP-RDFSource;
- _MemoryHandler_, exposing the percentage of free physical memory as LDP-RDFSource.

### Dependencies

_ldp-coap-core_ requires the following libraries:

- _californium-core-ldp_ (included in the LDP-CoAP project) for CoAP-based communication;
- [OpenRDF Sesame 2.8.10](http://rdf4j.org/) for in-memory RDF data management;
- [JSON-java](http://github.com/stleary/JSON-java) (v.20160212) to format data in JSON;
- [jsonld-java 0.8.2](http://github.com/jsonld-java) to format data according to [JSON-LD specification](http://www.w3.org/TR/json-ld/);
- [Apache Marmotta RDF Patch Util 3.3](http://marmotta.apache.org/sesame.html) to update RDF statements of a Sesame Repository according to the [RDF Patch](http://afs.github.io/rdf-patch/) format;

### Java SE Development Kit 

_ldp-coap-core_ was tested using Oracle Java SE Development Kit 8, Update 65 (JDK 8u65).

License
-------------

_ldp-coap-core_ is distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).