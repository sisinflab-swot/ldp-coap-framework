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
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.LDP.Code;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPContentFormatException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPPreconditionFailedException;

public class CoAPLDPRDFSource extends CoAPLDPResource {

	protected CoAPLDPResourceManager mng = null;	
	protected LDPDataHandler dh = null;

	public CoAPLDPRDFSource(String name, CoAPLDPResourceManager mng) {
		super(name);

		this.mng = mng;
		this.fRDFType = LDP.CLASS_RDFSOURCE;
		
		this.name = "/" + name;

		initRDFSource();
	}

	public CoAPLDPRDFSource(String name, String path, CoAPLDPResourceManager mng) {
		super(name);

		this.mng = mng;
		this.fRDFType = LDP.CLASS_RDFSOURCE;
		
		this.name = path + "/" + name;

		initRDFSource();
	}

	public CoAPLDPRDFSource(String name, CoAPLDPResourceManager mng, String type) {
		super(name);

		this.mng = mng;
		this.fRDFType = type;
		
		this.name = "/" + name;

		initRDFSource();
	}

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
	}

	public void handleGET(CoapExchange exchange) {
		
		Code m = getLDPMethod(exchange);
		
		if (m.equals(LDP.Code.GET)) {
			handleLDPGET(exchange);
		} else if (m.equals(LDP.Code.OPTIONS)) {
			handleLDPOPTIONS(exchange);
		} else if (m.equals(LDP.Code.HEAD)) {
			handleLDPHEAD(exchange);
		}
	}
	
	public void handlePUT(CoapExchange exchange) {
		
		Code m = getLDPMethod(exchange);
		
		if (m.equals(LDP.Code.PUT)) {
			handleLDPPUT(exchange);
		} else if (m.equals(LDP.Code.PATCH)) {
			handleLDPPATCH(exchange);
		}
	}

	protected void handleLDPPATCH(CoapExchange exchange) {
		List<byte[]> im = exchange.getRequestOptions().getIfMatch();
		int ct = exchange.getRequestOptions().getContentFormat();
		
		if(im.isEmpty()){
			exchange.respond(ResponseCode.PRECONDITION_REQUIRED);
		}		
		else if (options.isAcceptedPatch(ct)) {
			try {
				String data = mng.getTurtleResourceGraph(mng.getBaseURI() + this.getURI());
				String etag = calculateEtag(data);
				String ifm = new String(im.get(0), StandardCharsets.UTF_8);
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

	protected void handleLDPGET(CoapExchange exchange) {

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
			String[] prefs = atts.get(LDP.LINK_LDP_PREF_INCLUDE).split(" ");
			for(String p : prefs){
				pref.add(p);
			}
		}
		
		return pref;
	}
	
	private List<String> getPreferOmit(CoapExchange exchange) {
		List<String> pref = new ArrayList<String>();
		
		List<String> q = exchange.getRequestOptions().getUriQuery();
		HashMap<String, String> atts = serializeAttributes(q);
		if(atts.containsKey(LDP.LINK_LDP_PREF_OMIT)){;
			String[] prefs = atts.get(LDP.LINK_LDP_PREF_OMIT).split(" ");
			for(String p : prefs){
				pref.add(p);
			}
		}
		
		return pref;
	}

	protected void handleLDPOPTIONS(CoapExchange exchange) {
		try {
			String text = options.toJSONString();
			exchange.respond(ResponseCode.CONTENT, text, MediaTypeRegistry.APPLICATION_JSON);
		} catch (JSONException e) {
			e.printStackTrace();
			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
		}		
	}

	protected void handleLDPHEAD(CoapExchange exchange) {
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

	public void handleDELETE(CoapExchange exchange) {
		mng.deleteRDFSource(mng.getBaseURI() + this.getURI());
		this.delete();
		exchange.respond(ResponseCode.DELETED);
	}

	public void handleLDPPUT(CoapExchange exchange) {
		
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

	public void setRDFDescription(String description) {
		mng.setRDFDescription(mng.getBaseURI() + name, description);
	}

	public void setRDFTitle(String title) {
		mng.setRDFTitle(mng.getBaseURI() + name, title);
	}

	public void setRDFCreated() {
		mng.setRDFCreated(mng.getBaseURI() + name);
	}	
	
	private String getBodyError(){
		return "Link: <" + mng.getconstrainedByURI() + ">; rel=\"" + LDP.LINK_REL_CONSTRAINEDBY + "\"";
	}
		
	public void setDataHandler(LDPDataHandler dh){
		this.dh = dh;
		dh.init(this.getFullName(), this.mng);
		dh.start();
	}
	
	public void startPublishData(){
		if(dh != null)
			dh.start();
	}
	
	public void stopPublishData(){
		if(dh != null)
			dh.stop();
	}
}
