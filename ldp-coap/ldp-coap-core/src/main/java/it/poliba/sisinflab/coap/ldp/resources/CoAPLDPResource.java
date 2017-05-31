package it.poliba.sisinflab.coap.ldp.resources;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.LDPOptions;

/**
 * Represents an LDP Resource
 * <p> 
 * @see <a href="https://www.w3.org/TR/ldp/#ldpr-resource">#LDP Resource</a>
 *
 */

public abstract class CoAPLDPResource extends CoapResource {
	
	protected boolean DEBUG = true;
	
	protected String name;
	
	protected String fRDFType;	
	protected LDPOptions options;

	/**
	 * Creates a new LDP Resource.
	 *
	 * @param  name 	the name of the resource
	 * 
	 */
	public CoAPLDPResource(String name) {
		super(name);
		
		this.name = name;

		this.fRDFType = LDP.CLASS_RESOURCE;
		// set display name
        getAttributes().setTitle(name);		
		getAttributes().addResourceType(LDP.CLASS_RESOURCE);
		
		options = new LDPOptions();
	}
	
	public abstract String getEtag() throws NoSuchAlgorithmException;
	
	protected static String calculateEtag(final String s) throws java.security.NoSuchAlgorithmException {		
		return  String.format("W/\"%s\"", Integer.toHexString(s.hashCode()).substring(0, 4));
	}
	
	@Override
	public void handleRequest(final Exchange exchange) {
		CoapExchange e = new CoapExchange(exchange, this);
		LDP.Code code = getLDPMethod(e);
		
		if (!DEBUG) {
			Date d = new Date();
			System.out.println("\n[" + d.toString() + "] Received request");
			System.out.println(Utils.prettyPrint(e.advanced().getCurrentRequest()));
		}
		
		if (DEBUG) 
			System.out.println("REQ;" + System.currentTimeMillis() + ";" + e.advanced().getCurrentRequest().getMID() + ";" + code);
		
		switch (code) {
			case GET:		
				if (options.isAllowed(code))
					handleGET(new CoapExchange(exchange, this));
				else
					super.handleGET(new CoapExchange(exchange, this));
				break;
						
			case POST:
				if (options.isAllowed(code))
					handlePOST(new CoapExchange(exchange, this));
				else
					super.handlePOST(new CoapExchange(exchange, this));
				break;
				
			case PUT:		
				if (options.isAllowed(code))
					handlePUT(new CoapExchange(exchange, this));
				else
					super.handlePUT(new CoapExchange(exchange, this));
				break;
				
			case DELETE: 	
				if (options.isAllowed(code))
					handleDELETE(new CoapExchange(exchange, this));
				else
					super.handleDELETE(new CoapExchange(exchange, this));
				break;
				
			case OPTIONS: 	
				if (options.isAllowed(code))
					handleOPTIONS(new CoapExchange(exchange, this));
				else
					this.handleOPTIONS(new CoapExchange(exchange, this));
				break;
				
			case PATCH: 	
				if (options.isAllowed(code))
					handlePATCH(new CoapExchange(exchange, this));
				else
					this.handlePATCH(new CoapExchange(exchange, this));
				break;
				
			case HEAD:		
				if (options.isAllowed(code))
					handleHEAD(new CoapExchange(exchange, this));
				else
					this.handleHEAD(new CoapExchange(exchange, this));
				break;
		}
		
		if (DEBUG) 
			System.out.println("RES;" + System.currentTimeMillis() + ";" + e.advanced().getCurrentRequest().getMID() + ";" + code);
	}
		
	public void handleOPTIONS(CoapExchange exchange) {
		exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
	}
	
	public void handleHEAD(CoapExchange exchange) {
		exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
	}
	
	public void handlePATCH(CoapExchange exchange) {
		exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
	}

	protected LDP.Code getLDPMethod(CoapExchange exchange) {
		List<String> q = exchange.getRequestOptions().getUriQuery();
		HashMap<String, String> atts = serializeAttributes(q);
		String method = atts.get(LDP.LINK_LDP);

		if (method == null)
			return LDP.Code.valueOf(exchange.getRequestCode().value);
		else {
			switch (method) {
			case "patch":
				return LDP.Code.PATCH;
			case "head":
				return LDP.Code.HEAD;
			case "options":
				return LDP.Code.OPTIONS;
			default:
				return null;
			}
		}
	}
	
	protected HashMap<String, String> serializeAttributes(List<String> list) {
		HashMap<String, String> atts = new HashMap<String, String>();
		for (String a : list) {
			String[] s = a.split("=");
			atts.put(s[0], s[1]);
		}
		return atts;
	}
	
	/**
	 * Returns the resource name within the LDP-CoAP server (including the full path).
	 *
	 * @return the full resource name
	 * 
	 * @see LDP.Code
	 */
	public String getFullName(){
		return this.name;
	}
	
	public void setAllowedMethod(LDP.Code method, boolean allowed) {
		this.options.setAllowedMethod(method, allowed);
	}
}
