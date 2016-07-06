package it.poliba.sisinflab.coap.ldp.raspberry;

import it.poliba.sisinflab.coap.ldp.server.CoAPLDPTestSuiteServer;

/**
 * Test Server used to run evaluation on Raspberry Pi board according to the <a href="http://w3c.github.io/ldp-testsuite/">Test Suite for W3C Linked Data Platform 1.0</a> 
 *
 */

public class TestServerRaspberryPi {
	
	final static String BASE_URI = "coap://192.168.2.206:5683";

	public static void main(String[] args) {
		
		CoAPLDPTestSuiteServer server = new CoAPLDPTestSuiteServer(BASE_URI);
		server.start();

	}

}
