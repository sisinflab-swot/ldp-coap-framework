package it.poliba.sisinflab.coap.ldp;

import java.io.IOException;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;

import it.poliba.sisinflab.coap.ldp.server.CoAPLDPTestSuiteServer;

public class CoAPLPDTestSuite {
	
	final static String BASE_URI = "coap://192.168.0.116:5688";

	public static void main(String[] args) throws IOException {
		
		/*** Init Proxy ***/
		new LDPCrossProxy(); 
		
		/*Configuration config = Configuration.getStandard();
		config.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, 20000);

		CoAPLDPTestSuiteServer testServer = new CoAPLDPTestSuiteServer(BASE_URI, config, 5688);
		testServer.start();*/
		
		while(true);
	}

}
