LDP-CoAP: Linked Data Platform for the Constrained Application Protocol
===================

[W3C Linked Data Platform 1.0 specification](http://www.w3.org/TR/ldp/) defines resource management primitives for HTTP only, pushing into the background not-negligible 
use cases related to Web of Things (WoT) scenarios where HTTP-based communication and infrastructures are unfeasible. 

LDP-CoAP proposes a mapping of the LDP specification for [RFC 7252 Constrained Application Protocol](https://tools.ietf.org/html/rfc7252) (CoAP) 
and a complete Java-based framework to publish Linked Data on the WoT. 

A general translation of LDP-HTTP requests and responses is provided, as well as a fully comprehensive framework for HTTP-to-CoAP proxying. 

LDP-CoAP framework can be also tested using the [W3C Test Suite for LDP](http://w3c.github.io/ldp-testsuite/).

Modules
-------------

LDP-CoAP consists of the following sub-projects:

- _ldp-coap-core_: basic framework implementation including the proposed LDP-CoAP mapping;
- _californium-core-ldp_: a modified version of the [Californium CoAP framework](https://github.com/eclipse/californium). Californium-core library was extended to support LDP features;
- _ldp-coap-proxy_: a modified version of the [californium-proxy](http://github.com/eclipse/californium/tree/master/californium-proxy) used to translate LDP-HTTP request methods and headers 
into the corresponding LDP-CoAP ones and then map back LDP-CoAP responses to LDP-HTTP;
- _ldp-coap-android_: simple project using _ldp-coap-core_ on Android platform;
- _ldp-coap-raspberry_: usage examples exploiting _ldp-coap-core_ on a [Raspberry Pi board](http://www.raspberrypi.org/);

Usage with Eclipse and Maven
-------------

Each module can be imported as Maven project in Eclipse. Make sure to have the following plugins before importing LDP-CoAP projects:

- [Eclipse EGit](http://www.eclipse.org/egit/)
- [M2Eclipse - Maven Integration for Eclipse](http://www.eclipse.org/m2e/)

Documentation
-------------

Hands-on introduction to LDP-CoAP using [basic code samples](http://sisinflab.poliba.it/swottools/ldp-coap/usage.html) available on the project webpage.

More details about packages and methods can be found on the official [Javadoc](http://sisinflab.poliba.it/swottools/ldp-coap/docs/javadoc/v1_0/).

License
-------------

_ldp-coap-core_, _ldp-coap-android_ and _ldp-coap-raspberry_ modules are distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

_californium-core-ldp_ and _ldp-coap-proxy_ are distributed under the [Eclipse Public License, Version 1.0](https://www.eclipse.org/legal/epl-v10.html) as derived projects.


Contact
-------------

For more information, please visit the [LDP-CoAP webpage](http://sisinflab.poliba.it/swottools/ldp-coap/).


Contribute
-------------
The main purpose of this repository is to share and continue to improve the LDP-CoAP framework, making it easier to use. If you're interested in helping us any feedback you have about using LDP-CoAP would be greatly appreciated. There are only a few guidelines that we need contributors to follow reported in the CONTRIBUTING.md file.


---------
