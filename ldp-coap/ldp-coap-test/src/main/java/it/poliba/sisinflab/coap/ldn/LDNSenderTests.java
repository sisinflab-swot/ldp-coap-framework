package it.poliba.sisinflab.coap.ldn;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import it.poliba.sisinflab.coap.ldn.CoAPLDNSender;
import it.poliba.sisinflab.coap.ldp.LDPCrossProxy;

public class LDNSenderTests {
	
	final static String TEST_SERVER_URI = "http://192.168.2.216:3001/target/";
	final static String UUID = "874cd000-4140-11e7-a740-a3e19e89550e";
	
	public static void main(String[] args) throws Exception {
		
		/*** Init Proxy ***/
		LDPCrossProxy proxy = new LDPCrossProxy();
		String coap2http = "coap://" + proxy.getProxyAddress() + ":" + proxy.getProxyPort() + "/coap2http"; 
		
		CoAPLDNSender sender = new CoAPLDNSender();		
		CoapResponse resp;			
		
		/*** Discovery RDF Body ***/
		String inbox_body = null;
		if (proxy != null && sender != null) {			
			String target = TEST_SERVER_URI + UUID + "?discovery=rdf-body";
			resp = sender.sendGETRequest(coap2http, target, MediaTypeRegistry.TEXT_TURTLE);
			System.out.println(Utils.prettyPrint(resp));
			
			inbox_body = sender.getInboxFromRDFBody(resp);
		}
		
		/*** Send Notification after RDF Body discovery ***/
		if (inbox_body != null) {
			File file = new File(LDNSenderTests.class.getClassLoader().getResource("ldn/ex-sender.json").getFile());
			String payload = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			
			resp = sender.sendNotification(coap2http, inbox_body, payload, MediaTypeRegistry.APPLICATION_LD_JSON);
			System.out.println(Utils.prettyPrint(resp));
		}
		
		/*** Discovery Link Header ***/
		String inbox_link = null;
		if (proxy != null && sender != null) {
			String target = TEST_SERVER_URI + UUID + "?discovery=link-header";
			resp = sender.sendHEADRequest(coap2http, target);
			System.out.println(Utils.prettyPrint(resp));
			
			inbox_link = sender.getInboxFromHeader(resp);
		}
		
		/*** Send Notification after Link Header discovery ***/
		if (inbox_link != null) {
			File file = new File(LDNSenderTests.class.getClassLoader().getResource("ldn/ex-sender.json").getFile());
			String payload = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			
			resp = sender.sendNotification(coap2http, inbox_link, payload, MediaTypeRegistry.APPLICATION_LD_JSON);
			System.out.println(Utils.prettyPrint(resp));
		}		
		
		System.exit(0);
	}

}
