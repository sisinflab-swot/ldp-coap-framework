package it.poliba.sisinflab.coap.ldn;

import java.io.IOException;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;

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
		try {
			return super.post(payload, ct);
		} catch (ConnectorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}
	
	public CoapResponse sendNotification(String coap2http, String inbox, String payload, int ct) {		
		Request req = new Request(Code.POST);
		req.setURI(coap2http);
		req.getOptions()
			.setProxyUri(inbox)
			.setContentFormat(ct);
		req.setPayload(payload);
		try {
			return this.advanced(req);
		} catch (ConnectorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}	

}
