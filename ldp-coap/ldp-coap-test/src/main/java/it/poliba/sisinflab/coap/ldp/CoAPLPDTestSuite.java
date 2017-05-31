package it.poliba.sisinflab.coap.ldp;

import java.io.IOException;

import org.eclipse.californium.core.network.config.NetworkConfig;

import it.poliba.sisinflab.coap.ldp.server.CoAPLDPTestSuiteServer;

public class CoAPLPDTestSuite {
	
	final static String BASE_URI = "coap://192.168.2.16:5688";

	public static void main(String[] args) throws IOException {
		
		/*** Init Proxy ***/
		new LDPCrossProxy(); 

		/*** Init LDP-CoAP TestSuite Server ***/
		NetworkConfig config = NetworkConfig.createStandardWithoutFile();
		config.set(NetworkConfig.Keys.MAX_RESOURCE_BODY_SIZE, 20000);
		CoAPLDPTestSuiteServer testServer = new CoAPLDPTestSuiteServer(BASE_URI, config, 5688);
		testServer.start();
	}

}
