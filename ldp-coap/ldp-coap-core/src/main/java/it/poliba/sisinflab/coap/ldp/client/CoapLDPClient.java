package it.poliba.sisinflab.coap.ldp.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class CoapLDPClient extends CoapClient {
	
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
			uri = uri + "&ldp=option";
		else
			uri = uri + "?ldp=option";
		super.setURI(uri);
		return super.get();
	}

}
