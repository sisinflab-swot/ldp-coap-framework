package it.poliba.sisinflab.coap.ldp.raspberry;

import it.poliba.sisinflab.coap.ldp.server.CoAPLDPTestSuiteServer;

public class TestServerRaspberryPi {
	
	final static String BASE_URI = "coap://192.168.2.206:5683";

	public static void main(String[] args) {
		
		CoAPLDPTestSuiteServer server = new CoAPLDPTestSuiteServer(BASE_URI);
		server.start();

	}

}
