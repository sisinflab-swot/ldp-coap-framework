package it.poliba.sisinflab.coap.ldp.resources;

import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPPreconditionFailedException;

/**
 * Represents an LDP Non-RDF Source
 * <p> 
 * @see <a href="https://www.w3.org/TR/ldp/#ldpnr">#LDP Non-RDF Source</a>
 *
 */

public class CoAPLDPNonRDFSource extends CoAPLDPResource {
	
	CoAPLDPResourceManager mng;
	byte[] data = {};
	int ct;

	/**
	 * Creates a new LDP Non-RDF Source.
	 *
	 * @param  	name 	the name of the resource
	 * @param	mng		the reference resource manager
	 * @param	type	the resource type
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDPNonRDFSource(String name, CoAPLDPResourceManager mng, int type) {
		super(name);
		
		this.mng = mng;
		this.fRDFType = LDP.CLASS_NONRDFSOURCE;
		this.ct = type;
		
		getAttributes().addResourceType(LDP.CLASS_NONRDFSOURCE);
		getAttributes().addContentType(type);  
		
		initNonRDFSource();
	}
	
	private void initNonRDFSource(){
		options.setAllowedMethod(LDP.Code.GET, true);
		options.setAllowedMethod(LDP.Code.DELETE, true);
		options.setAllowedMethod(LDP.Code.HEAD, true);
		options.setAllowedMethod(LDP.Code.OPTIONS, true);
		options.setAllowedMethod(LDP.Code.PUT, true);
	}
	
	/**
	 * Sets the raw data of the resource 
	 *
	 * @param  	data 	the resource data
	 */
	public void setData(byte[] data){
		this.data = data;
		//System.out.println(">>> CoAPLDPNonRDFSource [" + this.name + "] Size: " + data.length + " bytes");
	}
	
    public void handleGET(CoapExchange exchange) {	
		
		try {
			exchange.setETag(calculateEtag(data.toString()).getBytes());
			
			if (ct == MediaTypeRegistry.TEXT_PLAIN)			
				exchange.respond(ResponseCode.CONTENT, new String(data, StandardCharsets.UTF_8), ct);    
			else
				exchange.respond(ResponseCode.CONTENT, data, ct); 	
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    }
	
    /**
	 * Manages LDP-CoAP DELETE requests.
	 *
	 * @param  exchange 	the request object
	 * 
	 * @see CoapExchange
	 * 
	 */
    public void handleDELETE(CoapExchange exchange) {    
		mng.deleteRDFSource(mng.getBaseURI() + this.getURI());
		this.delete();
		exchange.respond(ResponseCode.DELETED);         
    }
    
    public void handleOPTIONS(CoapExchange exchange) {
    	try {
			String text = options.toJSONString();
			exchange.respond(ResponseCode.CONTENT, text, MediaTypeRegistry.APPLICATION_JSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
	}
    
    public void handleHEAD(CoapExchange exchange) {
		List<byte[]> im = exchange.getRequestOptions().getIfMatch(); 

		try {
			String etag = calculateEtag(data.toString());
			if (!im.isEmpty()) {
				String ifm = new String(im.get(0), StandardCharsets.UTF_8);
				if (!etag.equals(ifm))
					throw new CoAPLDPPreconditionFailedException("Precondition Failed: If-Match");
			}

			exchange.setETag(etag.getBytes());
			exchange.respond(ResponseCode.VALID, "", ct);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
		} catch (CoAPLDPPreconditionFailedException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.PRECONDITION_FAILED);
		}		
	}

	public void handlePUT(CoapExchange exchange) {
		
		List<byte[]> im = exchange.getRequestOptions().getIfMatch();
		if(im.isEmpty()){
			exchange.respond(ResponseCode.PRECONDITION_REQUIRED);
		}		
		else if (exchange.getRequestOptions().getContentFormat() == ct) {
			
			try {
				String etag = calculateEtag(data.toString());
				String ifm = new String(im.get(0), StandardCharsets.UTF_8);
				if (!etag.equals(ifm))
					throw new CoAPLDPPreconditionFailedException("Precondition Failed: If-Match");
				
			} catch (CoAPLDPPreconditionFailedException e){
				e.printStackTrace();
				exchange.respond(ResponseCode.PRECONDITION_FAILED);
				return;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
				return;
			}
			
			data = exchange.getRequestPayload();
			exchange.respond(ResponseCode.CHANGED);

		} else
			exchange.respond(ResponseCode.BAD_OPTION);
	}

}
