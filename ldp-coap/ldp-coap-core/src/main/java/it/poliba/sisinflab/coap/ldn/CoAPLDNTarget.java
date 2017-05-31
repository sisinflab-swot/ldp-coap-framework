package it.poliba.sisinflab.coap.ldn;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPPreconditionFailedException;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPResourceManager;

public class CoAPLDNTarget extends CoAPLDPRDFSource {
	
	String inboxHeader;
	
	/**
	 * Creates a new LDN Target.
	 *
	 * @param  	name 	the name of the resource
	 * @param	mng		the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDNTarget(CoAPLDPResourceManager mng) {
		super("target", mng);
	}

	/**
	 * Creates a new LDP Target (as subresource).
	 *
	 * @param  	name 	the name of the resource
	 * @param	path	the path of the root resource
	 * @param	mng		the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDNTarget(String path, CoAPLDPResourceManager mng) {
		super("target", path, mng);
	}
	
	@Override
	protected void initAllowedMethods() {
		options.setAllowedMethod(LDP.Code.GET, true);
		options.setAllowedMethod(LDP.Code.OPTIONS, true);
		options.setAllowedMethod(LDP.Code.HEAD, true);
		
		getAttributes().addContentType(MediaTypeRegistry.APPLICATION_LD_JSON);
	}
	
	public void setInbox(String uri) {
		inboxHeader = uri;
		mng.updateRDFStatement(mng.getBaseURI() + this.name, LDP.PROP_INBOX, uri);
	}
	
	@Override
	public void handleHEAD(CoapExchange exchange) {
		List<byte[]> im = exchange.getRequestOptions().getIfMatch(); 
		try {
			String etag = getEtag();
			if (!im.isEmpty()) {
				String ifm = new String(im.get(0), StandardCharsets.UTF_8);
				if (!etag.equals(ifm))
					throw new CoAPLDPPreconditionFailedException("Precondition Failed: If-Match");
			}
			
			// According to the LDN-CoAP Mapping
			exchange.setLocationPath(inboxHeader);
			exchange.setLocationQuery(LDP.LINK_INBOX);

			exchange.setETag(etag.getBytes());			
			exchange.respond(ResponseCode.VALID, "", MediaTypeRegistry.TEXT_TURTLE);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
		} catch (CoAPLDPPreconditionFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exchange.respond(ResponseCode.PRECONDITION_FAILED);
		}		
	}

}
