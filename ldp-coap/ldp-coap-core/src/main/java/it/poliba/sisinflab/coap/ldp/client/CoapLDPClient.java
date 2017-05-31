package it.poliba.sisinflab.coap.ldp.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class CoAPLDPClient extends CoapClient {
	
	public CoapResponse head() {
		String uri = super.getURI();
		if (uri.contains("?"))
			uri = uri + "&ldp=head";
		else
			uri = uri + "?ldp=head";
		super.setURI(uri);
		return super.get();
	}
	
	public CoapResponse option() {
		String uri = super.getURI();
		if (uri.contains("?"))
			uri = uri + "&ldp=options";
		else
			uri = uri + "?ldp=options";
		super.setURI(uri);
		return super.get();
	}
	
	public CoapResponse patch(String payload, int format, byte[]... etags) {
		String uri = super.getURI();
		if (uri.contains("?"))
			uri = uri + "&ldp=patch";
		else
			uri = uri + "?ldp=patch";
		super.setURI(uri);
		return super.putIfMatch(payload, format, etags);
	}

}
