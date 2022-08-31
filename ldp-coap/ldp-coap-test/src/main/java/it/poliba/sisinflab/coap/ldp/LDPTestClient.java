package it.poliba.sisinflab.coap.ldp;

import org.eclipse.californium.core.WebLink;

import it.poliba.sisinflab.coap.ldp.client.CoAPLDPClient;

public class LDPTestClient {
	
	final static String SERVER_URI = "coap://192.168.0.216:5688";

	public static void main(String[] args) throws Exception {
		CoAPLDPClient client = new CoAPLDPClient();		
		client.setURI(SERVER_URI);
		
		for(WebLink link : client.discover()) {
			System.out.println(link.getURI());
		}
	}

}
