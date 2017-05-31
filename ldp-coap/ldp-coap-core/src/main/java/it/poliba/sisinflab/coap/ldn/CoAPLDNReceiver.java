package it.poliba.sisinflab.coap.ldn;

import org.eclipse.californium.core.network.config.NetworkConfig;

import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPResourceManager;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPServer;

public class CoAPLDNReceiver extends CoAPLDPServer {
	
	CoAPLDNInbox inbox = null;
	CoAPLDNTarget target = null;

	public CoAPLDNReceiver(String BASE_URI) {
		super(BASE_URI);
		init();
	}
	
	public CoAPLDNReceiver(CoAPLDPResourceManager rm) {
		super(rm);
		init();
	}
	
	public CoAPLDNReceiver(String BASE_URI, NetworkConfig config, int port) {
    	super(BASE_URI, config, port);
    	init();
    }
	
	private void init() {
		inbox = new CoAPLDNInbox(getCoAPLDPResourceManager());     
        inbox.setRDFCreated();
        getRoot().add(inbox);                  
	}	
	
	public CoAPLDNInbox getInbox() {
		return this.inbox;
	}
	
	public void setTarget(String inboxURI)  {
		if (target == null) {
			target = new CoAPLDNTarget(getCoAPLDPResourceManager());
        	target.setRDFCreated();
        	getRoot().add(target);
		}
        
		target.setInbox(inboxURI);
	}

}
