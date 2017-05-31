package it.poliba.sisinflab.coap.ldn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.client.CoAPLDPClient;

public class CoAPLDNConsumer extends CoAPLDPClient {
	
	public CoapResponse sendGETRequest(String coap2http, String uri, int accept) {
		Request req = new Request(Code.GET);
		req.setURI(coap2http);
		req.getOptions().setAccept(accept);
		req.getOptions().setProxyUri(uri);
		return this.advanced(req);
	}
	
	public CoapResponse sendGETRequest(String target, int accept) {
		this.setURI(target);
		return this.get(accept);
	}
	
	public CoapResponse sendHEADRequest(String coap2http, String target) {
		Request req = new Request(Code.GET); 
		req.getOptions().getUriQuery().add("ldp=head");
		req.setURI(coap2http);
		req.getOptions().setProxyUri(target);
		return this.advanced(req);
	}
	
	public CoapResponse sendHEADRequest(String target) {
		this.setURI(target);
		return this.head();
	}		

	public String getInboxFromHeader(CoapResponse resp) {		
		if ((resp.getCode().compareTo(ResponseCode.VALID) == 0) && 
			(resp.getOptions().getLocationQueryString().contains(LDP.LINK_INBOX))) {			
			return resp.getOptions().getLocationPathString();			
		} else
			return null;
	}

	public String getInboxFromRDFBody(CoapResponse resp) {
		try {			
			if ((resp.getCode().compareTo(ResponseCode.CONTENT) == 0) && (resp.getResponseText().length() > 0)) {
				int ct = resp.getOptions().getContentFormat();
				String rdf = resp.getResponseText();				
				ValueFactory f = SimpleValueFactory.getInstance();		
				InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
				
				Model m = null;
				if (ct == MediaTypeRegistry.APPLICATION_LD_JSON)
					m = Rio.parse(stream, LDP.getNSURI(), RDFFormat.JSONLD);
				else if (ct == MediaTypeRegistry.TEXT_TURTLE)
					m = Rio.parse(stream, LDP.getNSURI(), RDFFormat.TURTLE);
				else
					return null;

				if(m.contains(null, f.createIRI(LDP.PROP_INBOX), null)){
					return m.filter(null, f.createIRI(LDP.PROP_INBOX), null).iterator().next().getObject().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		
		return null;
	}
	
	public ArrayList<String> getNotificationList(CoapResponse resp) {
		ArrayList<String> notifications = new ArrayList<String>();
		
		try {			
			if ((resp.getCode().compareTo(ResponseCode.CONTENT) == 0) && (resp.getResponseText().length() > 0)) {
				int ct = resp.getOptions().getContentFormat();
				String rdf = resp.getResponseText();				
				ValueFactory f = SimpleValueFactory.getInstance();		
				InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
				
				Model m = null;
				if (ct == MediaTypeRegistry.APPLICATION_LD_JSON)
					m = Rio.parse(stream, LDP.getNSURI(), RDFFormat.JSONLD);
				else if (ct == MediaTypeRegistry.TEXT_TURTLE)
					m = Rio.parse(stream, LDP.getNSURI(), RDFFormat.TURTLE);

				Iterator<Statement> it = m.filter(null, f.createIRI(LDP.PROP_CONTAINS), null).iterator();
				while (it.hasNext()) {
					Statement s = it.next();
					notifications.add(s.getObject().stringValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		
		return notifications;
	}		

}
