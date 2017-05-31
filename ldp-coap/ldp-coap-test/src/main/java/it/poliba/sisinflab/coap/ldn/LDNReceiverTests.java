package it.poliba.sisinflab.coap.ldn;

import java.io.IOException;

import org.eclipse.californium.core.network.config.NetworkConfig;

import it.poliba.sisinflab.coap.ldn.CoAPLDNReceiver;
import it.poliba.sisinflab.coap.ldp.LDPCrossProxy;

public class LDNReceiverTests {
	
	final static String BASE_URI = "coap://192.168.2.16:5688";

	public static void main(String[] args) throws IOException {
		
		/*** Init Proxy ***/
		new LDPCrossProxy(); 

		/*** Init LDN Receiver ***/
		CoAPLDNReceiver receiver = new CoAPLDNReceiver(BASE_URI, NetworkConfig.createStandardWithoutFile(), 5688);
		receiver.setConstrainedByURI("http://sisinflab.poliba.it/swottools/ldp-coap/ldp-report.html");
		receiver.getInbox().createRDFSource("simple-notification");
		receiver.start();		
	}

}
