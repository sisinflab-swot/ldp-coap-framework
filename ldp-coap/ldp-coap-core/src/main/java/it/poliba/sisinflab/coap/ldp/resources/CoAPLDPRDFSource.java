package it.poliba.sisinflab.coap.ldp.resources;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.marmotta.platform.ldp.patch.InvalidPatchDocumentException;
import org.apache.marmotta.platform.ldp.patch.parser.ParseException;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.json.JSONException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPContentFormatException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPPreconditionFailedException;

/**
 * Represents an LDP RDF Source
 * <p> 
 * @see <a href="https://www.w3.org/TR/ldp/#ldprs">#LDP RDF Source</a>
 *
 */

public class CoAPLDPRDFSource extends CoAPLDPResource {

	protected CoAPLDPResourceManager mng = null;	
	protected LDPDataHandler dh = null;
	
	/**
	 * Creates a new LDP RDF Source.
	 *
	 * @param  	name 	the name of the resource
	 * @param	mng		the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDPRDFSource(String name, CoAPLDPResourceManager mng) {
		super(name);

		this.mng = mng;
		this.fRDFType = LDP.CLASS_RDFSOURCE;
		
		this.name = "/" + name;

		initRDFSource();
	}

	/**
	 * Creates a new LDP RDF Source (as subresource).
	 *
	 * @param  	name 	the name of the resource
	 * @param	path	the path of the root resource
	 * @param	mng		the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDPRDFSource(String name, String path, CoAPLDPResourceManager mng) {
		super(name);

		this.mng = mng;
		this.fRDFType = LDP.CLASS_RDFSOURCE;
		
		this.name = path + "/" + name;

		initRDFSource();
	}

	/**
	 * Creates a new LDP RDF Source with a specific type (in addition to the basic RDFSource type).
	 *
	 * @param  	name 	the name of the resource
	 * @param	mng		the reference resource manager
	 * @param	type	the additional type for the resource
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDPRDFSource(String name, CoAPLDPResourceManager mng, String type) {
		super(name);

		this.mng = mng;
		this.fRDFType = type;
		
		this.name = "/" + name;

		initRDFSource();
	}

	/**
	 * Creates a new LDP RDF Source (as subresource) with a specific type (in addition to the basic RDFSource type).
	 *
	 * @param  	name 	the name of the resource
	 * @param	path	the path of the root resource
	 * @param	mng		the reference resource manager
	 * @param	type	the additional type for the resource
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDPRDFSource(String name, String path, CoAPLDPResourceManager mng, String type) {
		super(name);

		this.mng = mng;
		this.fRDFType = type;
		
		this.name = path + "/" + name;

		initRDFSource();
	}

	protected void initRDFSource() {
		
		mng.addRDFSource(mng.getBaseURI() + this.name);
		getAttributes().addResourceType(LDP.CLASS_RDFSOURCE);
		
		if(!this.fRDFType.equals(LDP.CLASS_RDFSOURCE)){
			mng.addRDFSource(mng.getBaseURI() + this.name, this.fRDFType);
			getAttributes().addResourceType(fRDFType);	
		}			
		
		options.setAllowedMethod(LDP.Code.GET, true);
		options.setAllowedMethod(LDP.Code.PUT, true);
		options.setAllowedMethod(LDP.Code.DELETE, true);
		options.setAllowedMethod(LDP.Code.OPTIONS, true);
		options.setAllowedMethod(LDP.Code.HEAD, true);
		
		options.setAllowedMethod(LDP.Code.PATCH, true);
		options.addAcceptPatchType(MediaTypeRegistry.APPLICATION_RDF_PATCH);
		
		getAttributes().addContentType(MediaTypeRegistry.TEXT_TURTLE);
	}

	public void handlePATCH(CoapExchange exchange) {
		List<byte[]> im = exchange.getRequestOptions().getIfMatch();
		int ct = exchange.getRequestOptions().getContentFormat();
		
		if(im.isEmpty()){
			exchange.respond(ResponseCode.PRECONDITION_REQUIRED);
		}		
		else if (options.isAcceptedPatch(ct)) {
			try {
				String data = mng.getTurtleResourceGraph(mng.getBaseURI() + this.getURI());
				String etag = calculateEtag(data);
				String ifm = new String(im.get(im.size()-1), StandardCharsets.UTF_8);
				if (!etag.equals(ifm))
					throw new CoAPLDPPreconditionFailedException("Precondition Failed: If-Match");

				String body = exchange.getRequestText();	
				mng.patchResource(body);
				
				exchange.setETag(etag.getBytes());
				exchange.respond(ResponseCode.CHANGED);				
				
			} catch (NoSuchAlgorithmException | RepositoryException e) {				
				e.printStackTrace();
				exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
			} catch (CoAPLDPPreconditionFailedException e) {
				e.printStackTrace();
				exchange.respond(ResponseCode.PRECONDITION_FAILED);
			} catch (ParseException | InvalidPatchDocumentException e) {
				e.printStackTrace();
				exchange.respond(ResponseCode.BAD_REQUEST);
			} 
		} 
		else exchange.respond(ResponseCode.BAD_OPTION);		
	}

	public void handleGET(CoapExchange exchange) {

		List<byte[]> im = exchange.getRequestOptions().getIfMatch(); 
		
		List<String> prefIncl = getPreferInclude(exchange);
		List<String> prefOmit = getPreferOmit(exchange);
		
		String rdf = "";
		int accept = -1;
		
		try {
			if ((!exchange.getRequestOptions().hasAccept()) || (exchange.getRequestOptions().getAccept() == -1)
					|| (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.TEXT_TURTLE)) {

				accept = MediaTypeRegistry.TEXT_TURTLE;
				rdf = mng.getTurtleResourceGraph(mng.getBaseURI() + this.getURI(), prefIncl, prefOmit);
				
			} else if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_LD_JSON) {
				
				accept = MediaTypeRegistry.APPLICATION_LD_JSON;
				rdf = mng.getJSONLDResourceGraph(mng.getBaseURI() + this.getURI());				
				
			} else 
				throw new CoAPLDPContentFormatException("Unsupported Type!");
			
			String etag = calculateEtag(rdf);
			if (!im.isEmpty()) {
				String ifm = new String(im.get(0), StandardCharsets.UTF_8);
				if (!etag.equals(ifm))
					throw new CoAPLDPPreconditionFailedException("Precondition Failed: If-Match");
			}

			exchange.setETag(etag.getBytes());
			
			String prefApplied = "";
			for(String p : prefIncl){
				prefApplied = prefApplied + "ldp-incl:" + p + " ";
			}
			
			for(String p : prefOmit){
				prefApplied = prefApplied + "ldp-omit:" + p + " ";
			}
			
			if (prefApplied.length() > 0)
				exchange.setLocationQuery(prefApplied.trim());

			exchange.respond(ResponseCode.CONTENT, rdf, accept);
			
		} catch (CoAPLDPPreconditionFailedException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.PRECONDITION_FAILED);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
		} catch (CoAPLDPContentFormatException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.BAD_OPTION);
		}
	}

	private List<String> getPreferInclude(CoapExchange exchange) {
		List<String> pref = new ArrayList<String>();
		
		List<String> q = exchange.getRequestOptions().getUriQuery();
		HashMap<String, String> atts = serializeAttributes(q);
		if(atts.containsKey(LDP.LINK_LDP_PREF_INCLUDE)){;
			String[] prefs = atts.get(LDP.LINK_LDP_PREF_INCLUDE).replace("\"", "").split(" ");
			for(String p : prefs){
				pref.add(p.trim());
			}
		}
		
		return pref;
	}
	
	private List<String> getPreferOmit(CoapExchange exchange) {
		List<String> pref = new ArrayList<String>();
		
		List<String> q = exchange.getRequestOptions().getUriQuery();
		HashMap<String, String> atts = serializeAttributes(q);
		if(atts.containsKey(LDP.LINK_LDP_PREF_OMIT)){;
			String[] prefs = atts.get(LDP.LINK_LDP_PREF_OMIT).replace("\"", "").split(" ");
			for(String p : prefs){
				pref.add(p.trim());
			}
		}
		
		return pref;
	}

	public void handleOPTIONS(CoapExchange exchange) {
		try {
			String text = options.toJSONString();
			exchange.respond(ResponseCode.CONTENT, text, MediaTypeRegistry.APPLICATION_JSON);
		} catch (JSONException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
		}		
	}
	
	public void handleHEAD(CoapExchange exchange) {
		List<byte[]> im = exchange.getRequestOptions().getIfMatch(); 
		String rdf = mng.getTurtleResourceGraph(mng.getBaseURI() + this.getURI());
		try {
			String etag = calculateEtag(rdf);
			if (!im.isEmpty()) {
				String ifm = new String(im.get(0), StandardCharsets.UTF_8);
				if (!etag.equals(ifm))
					throw new CoAPLDPPreconditionFailedException("Precondition Failed: If-Match");
			}

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

	public void handlePUT(CoapExchange exchange) {
		
		List<byte[]> im = exchange.getRequestOptions().getIfMatch();
		if(im.isEmpty()){
			exchange.respond(ResponseCode.PRECONDITION_REQUIRED);
		}		
		else if (exchange.getRequestOptions().getContentFormat() == MediaTypeRegistry.TEXT_TURTLE) {
			
			try {
				String data = mng.getTurtleResourceGraph(mng.getBaseURI() + this.getURI());
				String etag = calculateEtag(data);
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

			String rdf = exchange.getRequestText();
			String res = mng.getBaseURI() + this.getURI();
			try {
				mng.putRDFSource(res, rdf, RDFFormat.TURTLE);

				for (Resource r : this.getChildren()) {
					mng.setLDPContainsRelationship(mng.getBaseURI() + r.getURI(), mng.getBaseURI() + getURI());
					System.out.println(r.getURI() + " --> " + mng.getBaseURI() + getURI());
				}

				exchange.respond(ResponseCode.CHANGED);
			} catch (CoAPLDPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exchange.respond(ResponseCode.FORBIDDEN, this.getBodyError());
			} catch (RDFParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exchange.respond(ResponseCode.BAD_REQUEST);
			} catch (RepositoryException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
			}
		} else
			exchange.respond(ResponseCode.BAD_OPTION);
	}	

	/**
	 * Sets the description RDF property for the resource.
	 *
	 * @param  description	the text to be used as description 
	 * 
	 */
	public void setRDFDescription(String description) {
		mng.setRDFDescription(mng.getBaseURI() + name, description);
	}

	/**
	 * Sets the title RDF property for the resource.
	 *
	 * @param  title	the text to be used as title
	 * 
	 */
	public void setRDFTitle(String title) {
		mng.setRDFTitle(mng.getBaseURI() + name, title);
	}

	/**
	 * Sets the created RDF property for the resource using current timestamp.
	 * 
	 */
	public void setRDFCreated() {
		mng.setRDFCreated(mng.getBaseURI() + name);
	}	
	
	private String getBodyError(){
		return "Link: <" + mng.getconstrainedByURI() + ">; rel=\"" + LDP.LINK_REL_CONSTRAINEDBY + "\"";
	}
		
	/**
	 * Sets a data handler to manage the RDF data
	 * 
	 * @param  dh	the data handler to be used
	 * 
	 * @see LDPDataHandler
	 * 
	 */
	public void setDataHandler(LDPDataHandler dh){
		this.dh = dh;
		dh.init(this.getFullName(), this.mng);
		dh.start();
	}
	
	/**
	 * Starts the publication of RDF data retrieved by the resource data handler
	 * 
	 */
	public void startPublishData(){
		if(dh != null)
			dh.start();
	}
	
	/**
	 * Stops the publication of RDF data retrieved by the resource data handler
	 * 
	 */
	public void stopPublishData(){
		if(dh != null)
			dh.stop();
	}
}
