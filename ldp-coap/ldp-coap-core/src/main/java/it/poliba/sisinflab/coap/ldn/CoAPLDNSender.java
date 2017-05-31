package it.poliba.sisinflab.coap.ldn;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;

/**
 * Represents an LDN Sender
 * <p> 
 * @see <a href="https://www.w3.org/TR/ldn/#sender">#LDN Sender</a>
 *
 */

public class CoAPLDNSender extends CoAPLDNConsumer {		
	
	/**
	 * Sends a notification to a specified Inbox.
	 *
	 * @param  	inbox 		the uri of the Inbox on the server
	 * @param	payload		the notification payload
	 * @param	ct			the Content-Format (ct) 
	 * 
	 * @return  the CoAP response
	 * 
	 * @see CoapResponse
	 */	
	public CoapResponse sendNotification(String inbox, String payload, int ct) {
		this.setURI(inbox);
		return super.post(payload, ct);		
	}
	
	public CoapResponse sendNotification(String coap2http, String inbox, String payload, int ct) {		
		Request req = new Request(Code.POST);
		req.setURI(coap2http);
		req.getOptions()
			.setProxyUri(inbox)
			.setContentFormat(ct);
		req.setPayload(payload);
		return this.advanced(req);	
	}	

}
