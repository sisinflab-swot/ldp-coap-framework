LDP-CoAP: Linked Data Platform for the Constrained Application Protocol
===================

[W3C Linked Data Platform 1.0 specification](http://www.w3.org/TR/ldp/) defines resource management primitives for HTTP only, pushing into the background not-negligible 
use cases related to Web of Things (WoT) scenarios where HTTP-based communication and infrastructures are unfeasible. 

LDP-CoAP proposes a mapping of the LDP specification for [RFC 7252 Constrained Application Protocol](https://tools.ietf.org/html/rfc7252) (CoAP) 
and a complete Java-based framework to publish Linked Data on the WoT. 

A general translation of LDP-HTTP requests and responses is provided, as well as a fully comprehensive framework for HTTP-to-CoAP proxying. 

LDP-CoAP framework also supports the [W3C Linked Data Notifications](https://www.w3.org/TR/ldn/) (LDN) protocol aiming to generate, share and reuse notifications across different applications.

LDP-CoAP functionalities can be tested using the [W3C Test Suite for LDP](http://w3c.github.io/ldp-testsuite/) and the [LDN Test Suite](http://github.com/csarven/ldn-tests).

Modules
-------------

LDP-CoAP (version 1.2.x) consists of the following sub-projects:

- _ldp-coap-core_: basic framework implementation including the proposed LDP-CoAP mapping;
- _ldp-coap-test_: includes reference client/server implementation used to test the framework according to the test suites cited above;
- _ldp-coap-raspberry_: usage examples exploiting _ldp-coap-core_ on a [Raspberry Pi 1 Model B+](https://www.raspberrypi.com/products/raspberry-pi-1-model-b-plus/) board;
- _ldp-coap-android_: simple project using _ldp-coap-core_ on Android platform;

LDP-CoAP also requires [Californium-LDP](https://github.com/sisinflab-swot/californium-ldp), a fork of the _Eclipse Californium_ framework supporting LDP specification. In particular, the following modules were defined as local Maven dependencies:

- _californium-core-ldp_: a modified version of the [californium-core](https://github.com/eclipse/californium) library extended to support LDP-CoAP features;
- _californium-proxy-ldp_: a modified version of the [californium-proxy](http://github.com/eclipse/californium) used to translate LDP-HTTP request methods and headers 
into the corresponding LDP-CoAP ones and then map back LDP-CoAP responses to LDP-HTTP;

Usage with Eclipse and Maven
-------------

Each module can be imported as Maven project in Eclipse. Make sure to have the following plugins before importing LDP-CoAP projects:

- [Eclipse EGit](http://www.eclipse.org/egit/)
- [M2Eclipse - Maven Integration for Eclipse](http://www.eclipse.org/m2e/)

Documentation
-------------

Hands-on introduction to LDP-CoAP using [basic code samples](http://swot.sisinflab.poliba.it/ldp-coap/usage.html) available on the project webpage.

More details about packages and methods can be found on the official [Javadoc](http://swot.sisinflab.poliba.it/ldp-coap/docs/javadoc/v1_1/).

References
-------------

If you want to refer to LDP-CoAP in a publication, please cite one of the following papers:

```
@InProceedings{ldp-coap-framework,
  author       = {Giuseppe Loseto and Saverio Ieva and Filippo Gramegna and Michele Ruta and Floriano Scioscia and Eugenio {Di Sciascio}},
  title        = {Linked Data (in low-resource) Platforms: a mapping for Constrained Application Protocol},
  booktitle    = {The Semantic Web - ISWC 2016: 15th International Semantic Web Conference, Proceedings, Part II},
  series       = {Lecture Notes in Computer Science},
  volume       = {9982},
  pages        = {131--139},
  month        = {oct},
  year         = {2016},
  editor       = {Paul Groth, Elena Simperl, Alasdair Gray, Marta Sabou, Markus Krotzsch, Freddy Lecue, Fabian Flock, Yolanda Gil},
  publisher    = {Springer International Publishing},
  address      = {Cham},
}
```

```
@InProceedings{ldp-coap-proposal,
  author = {Giuseppe Loseto and Saverio Ieva and Filippo Gramegna and Michele Ruta and Floriano Scioscia and Eugenio {Di Sciascio}},
  title = {Linking the Web of Things: LDP-CoAP mapping},
  booktitle = {The 7th International Conference on Ambient Systems, Networks and Technologies (ANT 2016) / Affiliated Workshops},
  series = {Procedia Computer Science},
  volume = {83},
  pages = {1182--1187},
  month = {may},
  year = {2016},
  editor = {Elhadi Shakshuki},
  publisher = {Elsevier}
}
```

License
-------------

_ldp-coap-core_, _ldp-coap-android_ and _ldp-coap-raspberry_ modules are distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

_californium-core-ldp_ and _ldp-coap-proxy_ are distributed under the [Eclipse Public License, Version 1.0](https://www.eclipse.org/legal/epl-v10.html) as derived projects.


Contact
-------------

For more information, please visit the [LDP-CoAP webpage](http://swot.sisinflab.poliba.it/ldp-coap/).


Contribute
-------------
The main purpose of this repository is to share and continue to improve the LDP-CoAP framework, making it easier to use. If you're interested in helping us any feedback you have about using LDP-CoAP would be greatly appreciated. There are only a few guidelines that we need contributors to follow reported in the CONTRIBUTING.md file.

---------
