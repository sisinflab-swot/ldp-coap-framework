package it.poliba.sisinflab.coap.ldn;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import it.poliba.sisinflab.coap.ldn.CoAPLDNReceiver;
import it.poliba.sisinflab.coap.ldn.CoAPLDNSender;

public class LDNExample {
	
	final static String BASE_URI = "coap://192.168.2.16:5688";	
	final static String TARGET_URI = BASE_URI + "/target";
	final static String INBOX_URI = BASE_URI + "/inbox";	
	
	static CoAPLDNReceiver receiver;
	static CoAPLDNSender sender;

	public static void main(String[] args) throws Exception {
		
		CoapResponse resp;
		
		sender = new CoAPLDNSender();
		
		/*receiver = new CoAPLDNReceiver(BASE_URI);
		receiver.setTarget(INBOX_URI);
        receiver.start();*/
		
        Thread.sleep(1000);
        
        // Discover Inbox from CoAP Header
        /*resp = sender.sendHEADRequest(TARGET_URI);
        String inbox = sender.getInboxFromHeader(resp);*/
 		
 		// Discover Inbox from RDF Body  	        
        /*resp = sender.get(MediaTypeRegistry.TEXT_TURTLE);
 		String inbox = sender.getInboxFromBody(resp); 		
 		System.out.println("Discovered Inbox: " + inbox);*/
        
        // Get Inbox headers via OPTION
        /*sender.setURI(inbox);
 		resp = sender.option();
 		System.out.println(resp.getCode().toString() + "\t" + resp.getCode().name());
 		System.out.println(resp.getResponseText());*/
        
        // Send a notification to an Inbox         
		/*File file = new File(LDNExample.class.getClassLoader().getResource("ldn/ex-sender.json").getFile());
		String payload = FileUtils.readFileToString(file, StandardCharsets.UTF_8);		
		sender.setURI(inbox);
		resp = sender.post(payload, MediaTypeRegistry.APPLICATION_LD_JSON);
		
		System.out.println(resp.getCode().toString() + "\t" + resp.getCode().name());
		System.out.println(resp.getOptions().getLocationPathString());*/
		
		// Get Inbox notifications
		resp = sender.sendGETRequest(INBOX_URI, MediaTypeRegistry.APPLICATION_LD_JSON);
		System.out.println(resp.getCode().toString() + "\t" + resp.getCode().name());
 		System.out.println(resp.getResponseText());	
		
		
		//receiver.shutdown();
		//System.exit(0);
	}

}
