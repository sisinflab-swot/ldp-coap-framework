ldp-coap-android
===================

Usage examples exploiting _ldp-coap-core_ on Android devices.

Developed with Android Studio 2.1 Beta 3.

![Android logo](https://www.android.com/static/img/logos-2x/android-wordmark-8EC047.png "Android logo")

Reference Platform
-------------

_ldp-coap-android_ was tested using the following configuration:

- Device: [Google LG Nexus 4](https://it.wikipedia.org/wiki/Nexus_4)
- Operating System: Android Lollipop 5.1.1

Implementation
-------------

_ldp-coap-android_ includes a simple activity with a running _CoAPLDPServer_ exposing different LDP resources: 
RDFSource, NonRDFSource, Basic Container, Direct Container and Indirect Container.

Also a _GenericSensorHandler_ was implemented to expose both hardware and software-based smartphone sensors as LDP-RDFSource. 
[Android sensor framework](http://developer.android.com/guide/topics/sensors/sensors_overview.html) is exploited to handle data from:

- Environment Sensors (light and pressure)
- Motion sensors (accelerometer, gyroscope and step counter)
- Position Sensors (proximity and orientation)

Dependencies
-------------

To build _ldp-coap-android_ with Android Studio the following JAR libraries must be included into the _app/libs_ folder:

- _ldp-coap-core-0.0.1_
- _californium-core-ldp-1.0.0_ 

Both are included in the LDP-CoAP project repository and should be compiled using Java7 JDK to avoid possible incompatibility with Android Studio. 
A pre-compiled version of these librearies is also provided. 

### _gradle_ dependency management
 
Other libraries are directly imported as _jcenter_ dependencies through the _build.gradle_ configuration file.

#### CoAP-based communication
- [element-connector-1.0.3](http://mvnrepository.com/artifact/org.eclipse.californium/element-connector) (Java socket abstraction for datagram transports)

#### In-memory RDF data management
- several module of [openrdf-sesame-2.8.10](http://rdf4j.org/) (Java framework for processing and handling RDF data)
- [slf4j-api-1.7.20](http://mvnrepository.com/artifact/org.slf4j/slf4j-api) (SLF4J API Module)
- [slf4j-android-1.7.20](http://mvnrepository.com/artifact/org.slf4j/slf4j-android) (SLF4J Android Binding)
- [commons-io-2.4](http://mvnrepository.com/artifact/commons-io/commons-io) (Commons IO library)
- [httpcore-4.4.4](http://mvnrepository.com/artifact/org.apache.httpcomponents/httpcore) (Apache HttpComponents Core)
- [httpclient-4.5.2](http://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient) (Apache HttpComponents Client)

#### [RDF Patch](http://afs.github.io/rdf-patch/) format support
- [marmotta-util-rdfpatch-3.3.0](http://mvnrepository.com/artifact/org.apache.marmotta/marmotta-util-rdfpatch) (Marmotta RDF Patch Util to update RDF statements of a Sesame repository)

#### [JSON-LD](http://www.w3.org/TR/json-ld/) specification support
- [jsonld-java-0.8.2](http://github.com/jsonld-java) (Json-LD core implementation);
- [jsonld-java-tools-0.8.2](http://mvnrepository.com/artifact/com.github.jsonld-java/jsonld-java-tools) (JSON-LD Java tools)
- [jsonld-java-sesame-0.5.1](http://mvnrepository.com/artifact/com.github.jsonld-java/jsonld-java-sesame) (JSON-LD Java integration module for Sesame)
- [jackson-core-2.7.3](http://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core) (Core Jackson abstractions, basic JSON streaming API implementation)
- [jackson-databind-2.7.3](http://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) (General data-binding functionality for Jackson: works on core streaming API)
- [jackson-annotations-2.7.3](http://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) (Core annotations used for value types, used by Jackson data binding package)

#### Android Patch: XML DataType
- Java source code provided by _it-tidalwave-android-javax-xml-datatype.jar_ was directly added to the _ldp-coap-android_ source code folder because required classes must be placed 
into the _org.apache.xerces.jaxp.datatype_ package and not in the _it.tidalwave.android.org.apache.xerces.jaxp.datatype_ package (as in the JAR file).

License
-------------

_ldp-coap-android_ is distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).