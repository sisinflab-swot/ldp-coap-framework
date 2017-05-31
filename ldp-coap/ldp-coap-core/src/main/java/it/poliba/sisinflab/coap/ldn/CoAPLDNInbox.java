package it.poliba.sisinflab.coap.ldn;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.rdf4j.rio.RDFFormat;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPResourceManager;

public class CoAPLDNInbox extends CoAPLDPBasicContainer {

	public CoAPLDNInbox(CoAPLDPResourceManager mng) {
		super("inbox", mng); 
	}
	
	@Override
	protected void initAdditionalAcceptPostType() {					
		/*
		 * Set additional Accept-Post Content-Format here
		 */
	}
	
	@Override
	protected String getAnonymousResource() {
		return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 8);
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		if (mng.getconstrainedByURI() != null) {
			exchange.setLocationQuery(LDP.LINK_CONSTRAINEDBY);
			exchange.setLocationPath(mng.getconstrainedByURI());
		}		
		super.handleGET(exchange);
	}
	
	@Override
	public void handleHEAD(CoapExchange exchange) {
		if (this.mng.getconstrainedByURI() != null) {
			exchange.setLocationQuery(LDP.LINK_CONSTRAINEDBY);
			exchange.setLocationPath(mng.getconstrainedByURI());
		}		
		super.handleHEAD(exchange);
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		if (checkConstraints(exchange)) {
			super.handlePOST(exchange);
		} else 
			exchange.respond(ResponseCode.BAD_REQUEST);
		
	}

	protected boolean checkConstraints(CoapExchange exchange) {
		try {
			String rdf = exchange.getRequestText();
			int ct = exchange.getRequestOptions().getContentFormat();
			
			if(rdf != null && ct != -1) {
				if (ct == MediaTypeRegistry.TEXT_TURTLE)
					return mng.verifyRelation(rdf, "https://www.w3.org/ns/activitystreams#actor", RDFFormat.TURTLE);
				else if (ct == MediaTypeRegistry.APPLICATION_LD_JSON)
					return mng.verifyRelation(rdf, "https://www.w3.org/ns/activitystreams#actor", RDFFormat.JSONLD);
				else 
					return false;	
			}
		} catch (Exception e) {
			return false;
		}
		
		return false;
	}

}
