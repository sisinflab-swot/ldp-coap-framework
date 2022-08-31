ldp-coap-android
===================

Usage examples exploiting _ldp-coap-core_ on Android devices.

Developed with Android Studio 2021.2.1.

![Android logo](https://www.android.com/static/img/logos-2x/android-wordmark-8EC047.png "Android logo")

Reference Platform
-------------

_ldp-coap-android_ was tested using the following configuration:

- Device: [LG Nexus 5X](https://en.wikipedia.org/wiki/Nexus_5X)
- Operating System: [Android 8.1 "Oreo"](https://www.android.com/versions/oreo-8-0/)

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

- _ldp-coap-core-1.3.0_
- _californium-core-ldp-3.3.0_ 

Both are included in the LDP-CoAP project repository and should be compiled using Java7 JDK to avoid possible incompatibility with Android Studio. 
A pre-compiled version of these libraries is also provided. 

### _gradle_ dependency management
 
Other libraries are directly imported as _jcenter_ dependencies through the _build.gradle_ configuration file.

#### CoAP-based communication
- [element-connector-3.3.0](http://mvnrepository.com/artifact/org.eclipse.californium/element-connector) (Java socket abstraction for datagram transports)

#### In-memory RDF data management
- several module of [RDF4J-3.7.4](http://rdf4j.org/) (Java framework for processing and handling RDF data)
- [slf4j-api-1.7.36](http://mvnrepository.com/artifact/org.slf4j/slf4j-api) (SLF4J API Module)
- [slf4j-android-1.7.36](http://mvnrepository.com/artifact/org.slf4j/slf4j-android) (SLF4J Android Binding)
- [commons-io-2.11.0](http://mvnrepository.com/artifact/commons-io/commons-io) (Commons IO library)
- [httpcore-4.4.15](http://mvnrepository.com/artifact/org.apache.httpcomponents/httpcore) (Apache HttpComponents Core)
- [httpclient-4.5.13](http://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient) (Apache HttpComponents Client)

#### [RDF Patch](http://afs.github.io/rdf-patch/) format support
- modified Apache [marmotta-util-rdfpatch module](https://github.com/sisinflab-swot/marmotta-util-rdfpatch-rdf4j) for Eclipse RDF4J (Marmotta RDF Patch Util to update RDF statements of a Sesame repository)

#### [JSON-LD](http://www.w3.org/TR/json-ld/) specification support
- RDF4J: Rio JSON-LD to format data according to JSON-LD specification;

#### Android Patch: XML DataType
- Java source code provided by _it-tidalwave-android-javax-xml-datatype.jar_ was directly added to the _ldp-coap-android_ source code folder because required classes must be placed 
into the _org.apache.xerces.jaxp.datatype_ package and not in the _it.tidalwave.android.org.apache.xerces.jaxp.datatype_ package (as in the JAR file).

License
-------------

_ldp-coap-android_ is distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).