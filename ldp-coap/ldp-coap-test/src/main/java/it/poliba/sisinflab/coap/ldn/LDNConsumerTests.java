package it.poliba.sisinflab.coap.ldn;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import it.poliba.sisinflab.coap.ldn.CoAPLDNConsumer;
import it.poliba.sisinflab.coap.ldp.LDPCrossProxy;

/*
 * LDN Tests for Consumers
 */
public class LDNConsumerTests {
	
	final static String TEST_SERVER_URI = "http://192.168.2.216:3001/";
	
	public static void main(String[] args) throws Exception {
		
		/*** Init Proxy ***/
		LDPCrossProxy proxy = new LDPCrossProxy();
		String coap2http = "coap://" + proxy.getProxyAddress() + ":" + proxy.getProxyPort() + "/coap2http"; 
		
		CoAPLDNConsumer consumer = new CoAPLDNConsumer();		
		CoapResponse resp;			
		
		/*** Test 01 - Discovery Target A Inbox Url from Link Header ***/
		String inbox_link = null;
		if (proxy != null && consumer != null) {
			String target = TEST_SERVER_URI + "discover-inbox-link-header";
			resp = consumer.sendHEADRequest(coap2http, target);			
			inbox_link = consumer.getInboxFromHeader(resp);
			
			System.out.println("\n01 - URL of the Inbox from target A (in header):\n" + inbox_link);
		}
		
		/*** Test 02 - Discovery Target B Inbox Url from RDF Body ***/
		String inbox_rdf = null;
		if (proxy != null && consumer != null) {
			String target = TEST_SERVER_URI + "discover-inbox-rdf-body";
			resp = consumer.sendGETRequest(coap2http, target, MediaTypeRegistry.APPLICATION_LD_JSON);			
			inbox_rdf = consumer.getInboxFromRDFBody(resp);
			
			System.out.println("\n02 - URL of the Inbox from target B (in RDF body):\n" + inbox_rdf);
		}
		
		/*** Test 03 - Retrieve notifications from Target A Inbox ***/
		if (inbox_link != null) {
			resp = consumer.sendGETRequest(coap2http, inbox_link, MediaTypeRegistry.APPLICATION_LD_JSON);
			ArrayList<String> notifications = consumer.getNotificationList(resp);
			
			System.out.println("\n03 - URLs of the notifications in target A's Inbox (JSON-LD compacted):\n" + 
					notifications.toString().replace(",", ""));
			
			for(String n : notifications) {
				resp = consumer.sendGETRequest(coap2http, n, MediaTypeRegistry.APPLICATION_LD_JSON);
				
				Path path = Paths.get(URI.create(n).getPath());			
				System.out.println("\n03/RES - Contents of the <" + path.getFileName().toString() + "> notification discovered from target A's Inbox:\n" + 
					resp.getResponseText());
			}
		}	
		
		/*** Test 04 - Retrieve notifications from Target B Inbox ***/
		if (inbox_link != null) {
			resp = consumer.sendGETRequest(coap2http, inbox_rdf, MediaTypeRegistry.APPLICATION_LD_JSON);
			ArrayList<String> notifications = consumer.getNotificationList(resp);
			
			System.out.println("\n04 - URLs of the notifications in target B's Inbox (JSON-LD expanded):\n" + 
					notifications.toString().replace(",", ""));
			
			for(String n : notifications) {
				resp = consumer.sendGETRequest(coap2http, n, MediaTypeRegistry.APPLICATION_LD_JSON);
				
				Path path = Paths.get(URI.create(n).getPath());			
				System.out.println("\n04/RES - Contents of the <" + path.getFileName().toString() + "> notification discovered from target B's Inbox:\n" + 
					resp.getResponseText());
			}
		}	
		
		System.exit(0);
	}

}
