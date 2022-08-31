package it.poliba.sisinflab.coap.ldp.client;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.elements.exception.ConnectorException;

public class CoAPLDPClient extends CoapClient {
	
	public CoapResponse head() {
		String uri = super.getURI();
		if (uri.contains("?"))
			uri = uri + "&ldp=head";
		else
			uri = uri + "?ldp=head";
		super.setURI(uri);
		try {
			return super.get();
		} catch (ConnectorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public CoapResponse option() {
		String uri = super.getURI();
		if (uri.contains("?"))
			uri = uri + "&ldp=options";
		else
			uri = uri + "?ldp=options";
		super.setURI(uri);
		try {
			return super.get();
		} catch (ConnectorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public CoapResponse patch(String payload, int format, byte[]... etags) {
		String uri = super.getURI();
		if (uri.contains("?"))
			uri = uri + "&ldp=patch";
		else
			uri = uri + "?ldp=patch";
		super.setURI(uri);
		try {
			return super.putIfMatch(payload, format, etags);
		} catch (ConnectorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
