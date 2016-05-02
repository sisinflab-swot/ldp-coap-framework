package it.poliba.sisinflab.coap.ldp.resources;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.server.resources.CoapExchange;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.LDPOptions;

public abstract class CoAPLDPResource extends CoapResource {
	
	protected String name;
	
	protected String fRDFType;	
	protected LDPOptions options;

	public CoAPLDPResource(String name) {
		super(name);
		
		this.name = name;

		this.fRDFType = LDP.CLASS_RESOURCE;
		// set display name
        getAttributes().setTitle(name);		
		getAttributes().addResourceType(LDP.CLASS_RESOURCE);
		
		options = new LDPOptions();
	}
	
	protected static String calculateEtag(final String s) throws java.security.NoSuchAlgorithmException {		
		return  String.format("W/\"%s\"", Integer.toHexString(s.hashCode()).substring(0, 4));
	}

	protected LDP.Code getLDPMethod(CoapExchange exchange) {
		
		Date d = new Date();
		System.out.println("\n[" + d.toString() + "] Received request");
		System.out.println(Utils.prettyPrint(exchange.advanced().getCurrentRequest()));
		
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
	
	public String getFullName(){
		return this.name;
	}
}
