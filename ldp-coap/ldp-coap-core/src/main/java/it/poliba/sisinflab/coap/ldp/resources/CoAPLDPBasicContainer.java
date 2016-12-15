package it.poliba.sisinflab.coap.ldp.resources;

import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;

import java.io.IOException;
import java.util.HashMap;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPContentFormatException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;

/**
 * Represents an LDP Basic Container
 * <p> 
 * @see <a href="https://www.w3.org/TR/ldp/#ldpbc">#LDP Basic Container</a>
 *
 */

public class CoAPLDPBasicContainer extends CoAPLDPContainer {

	/**
	 * Creates a new LDP Basic Container.
	 *
	 * @param  	name 	the name of the resource
	 * @param	mng		the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDPBasicContainer(String name, CoAPLDPResourceManager mng) {
		super(name, "", mng);
		this.name = "/" + name;
		initBasicContainer();
	}

	/**
	 * Creates a new LDP Basic Container (as subresource).
	 *
	 * @param  	name 	the name of the resource
	 * @param	path	the path of the root resource
	 * @param	mng		the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public CoAPLDPBasicContainer(String name, String path, CoAPLDPResourceManager mng) {
		super(name, "", mng);
		this.name = path + "/" + name;
		initBasicContainer();
	}

	private void initBasicContainer() {
		this.fRDFType = LDP.CLASS_BASIC_CONTAINER;

		getAttributes().addResourceType(LDP.CLASS_BASIC_CONTAINER);

		mng.addRDFBasicContainer(mng.getBaseURI() + this.getFullName());

		options.setAllowedMethod(LDP.Code.POST, true);

		options.addAcceptPostType(MediaTypeRegistry.TEXT_TURTLE);
		options.addAcceptPostType(MediaTypeRegistry.APPLICATION_LD_JSON);
		options.addAcceptPostType(MediaTypeRegistry.TEXT_PLAIN);
		options.addAcceptPostType(MediaTypeRegistry.IMAGE_PNG);
	}

	/**
	 * Manages LDP-CoAP POST requests.
	 *
	 * @param  exchange 	the request object
	 * 
	 * @see CoapExchange
	 * 
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		this.postResource(exchange, false);
	}

	@Override
	protected void handleLDPPutToCreate(CoapExchange exchange) {
		this.postResource(exchange, true);
	}
	
	private void postResource(CoapExchange exchange, boolean putToCreate){
		Request req = exchange.advanced().getCurrentRequest();
		HashMap<String, String> atts = serializeAttributes(req.getOptions().getUriQuery());

		String title = atts.get(LinkFormat.TITLE);
		if (title == null) {
			title = getAnonymousResource();
		}

		int ct = exchange.getRequestOptions().getContentFormat();
		String rt = atts.get(LinkFormat.RESOURCE_TYPE);

		if ((ct != -1) && (title != null)) {

			try {
				String childName = getURI() + "/" + title;

				if (mng.isDeleted(childName)) {
					if (!putToCreate){
						title = getAnonymousResource();
						childName = getURI() + "/" + title;
					} else {
						throw new CoAPLDPException("LDP Resource previously deleted!");
					}
				}

				if (!existChild(childName)) {

					this.addNewResource(exchange, ct, rt, childName, title);
					mng.setLDPContainsRelationship(mng.getBaseURI() + childName, mng.getBaseURI() + getURI());

					exchange.setLocationPath(childName);
					exchange.setLocationQuery(
							LinkFormat.RESOURCE_TYPE + "=" + LDP.LINK_LDP + ":" + LDP.CLASS_LNAME_RESOURCE);
					exchange.respond(ResponseCode.CREATED);
				} else
					exchange.respond(ResponseCode.FORBIDDEN);

			} catch (CoAPLDPContentFormatException e) {
				e.printStackTrace();
				exchange.respond(ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
			} catch (RDFParseException | CoAPLDPException e) {
				e.printStackTrace();
				exchange.respond(ResponseCode.BAD_REQUEST);
			} catch (RepositoryException | IOException e) {
				e.printStackTrace();
				exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
			}
		} else {
			exchange.respond(ResponseCode.BAD_REQUEST);
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
		mng.deleteRDFBasicContainer(mng.getBaseURI() + this.getURI());
		// mng.deleteRDFSource(mng.getBaseURI() + this.getURI());
		this.delete();
		exchange.respond(ResponseCode.DELETED);
	}

	protected boolean existChild(String childName) {
		for (Resource r : this.getChildren()) {
			if (r.getURI().equals(childName))
				return true;
		}
		return false;
	}

	protected void addNewResource(CoapExchange exchange, int ct, String rt, String childName, String title)
			throws RDFParseException, RepositoryException, IOException, CoAPLDPException {
		if (ct == MediaTypeRegistry.TEXT_TURTLE || ct == MediaTypeRegistry.APPLICATION_LD_JSON) {

			RDFFormat f;
			if (ct == MediaTypeRegistry.TEXT_TURTLE)
				f = RDFFormat.TURTLE;
			else
				f = RDFFormat.JSONLD;

			String body = exchange.getRequestText();
			mng.postRDFSource(mng.getBaseURI() + childName, body, f);

			if ((rt == null) || (rt.equals(LDP.LINK_LDP + ":" + LDP.CLASS_LNAME_RESOURCE))) {
				/*** Add LDP-RDFSource ***/
				add(new CoAPLDPRDFSource(title, getFullName(), mng));
			} else if (rt.equals(LDP.LINK_LDP + ":" + LDP.CLASS_LNAME_BASIC_CONTAINER)) {
				/*** Add LDP-BasicContainer ***/
				CoAPLDPBasicContainer bc = new CoAPLDPBasicContainer(title, mng);
				bc.setRDFCreated();
				add(bc);
			} else if (rt.equals(LDP.LINK_LDP + ":" + LDP.CLASS_LNAME_DIRECT_CONTAINER)) {
				String memberRel = mng.getMemberRelation(body, f);
				String isMemberOfRel = mng.getIsMemberOfRelation(body, f);
				String resName = mng.getMemberResource(body, f);

				if (resName.equals(mng.getBaseURI())) {
					mng.deleteMemberResourceStatement(childName);
					resName = "resource";
				}

				/*** Add LDP-DirectContainer ***/
				CoAPLDPRDFSource memberRes = new CoAPLDPRDFSource(resName, childName, mng);
				CoAPLDPDirectContainer dc = new CoAPLDPDirectContainer(title, this.getURI(), mng, memberRes, memberRel,
						isMemberOfRel);
				dc.setRDFCreated();
				add(dc);
			} else
				throw new CoAPLDPException("Invalid RT query parameter.");
		} else if (options.getAcceptedPostTypes().contains(ct)) {
			/*** Add LDP-NonRDFSource ***/
			CoAPLDPNonRDFSource nRDF = new CoAPLDPNonRDFSource(title, mng, ct);
			nRDF.setData(exchange.getRequestPayload());
			add(nRDF);
		} else
			throw new CoAPLDPContentFormatException("Content-Format (CT) Not Accepted.");
	}

	public CoAPLDPRDFSource createRDFSource(String name) {
		CoAPLDPRDFSource res = new CoAPLDPRDFSource(name, getFullName(), mng);
		mng.setLDPContainsRelationship(mng.getBaseURI() + res.getFullName(), mng.getBaseURI() + getFullName());
		add(res);
		return res;
	}

	public CoAPLDPRDFSource createRDFSource(String name, String type) {
		CoAPLDPRDFSource res = new CoAPLDPRDFSource(name, getFullName(), mng, type);
		mng.setLDPContainsRelationship(mng.getBaseURI() + res.getFullName(), mng.getBaseURI() + getFullName());
		add(res);
		return res;
	}

	public CoAPLDPBasicContainer createBasicContainer(String name) {
		CoAPLDPBasicContainer bc = new CoAPLDPBasicContainer(name, this.getFullName(), mng);
		mng.setLDPContainsRelationship(mng.getBaseURI() + bc.getFullName(), mng.getBaseURI() + getFullName());
		add(bc);
		return bc;
	}

	public CoAPLDPNonRDFSource createNonRDFSource(String name, int mediaType) {
		CoAPLDPNonRDFSource nr = new CoAPLDPNonRDFSource(name, mng, mediaType);
		mng.setLDPContainsRelationship(mng.getBaseURI() + "/" + nr.getURI(), mng.getBaseURI() + getFullName());
		add(nr);
		return nr;
	}

	public CoAPLDPDirectContainer createDirectContainer(String name, String member, String memberType,
			String memberRelation, String isMemberOfRelation) {
		CoAPLDPRDFSource memberRes = new CoAPLDPRDFSource(member, this.getFullName() + "/" + name, mng, memberType);
		CoAPLDPDirectContainer dc = null;
		try {
			dc = new CoAPLDPDirectContainer(name, this.getFullName(), mng, memberRes, memberRelation,
					isMemberOfRelation);
			add(dc);
			mng.setLDPContainsRelationship(mng.getBaseURI() + dc.getFullName(), mng.getBaseURI() + getFullName());
		} catch (CoAPLDPException e) {
			e.printStackTrace();
		}
		return dc;
	}

	public CoAPLDPIndirectContainer createIndirectContainer(String name, String member, String memberType,
			String memberRelation, String insertedContentRelation) {
		CoAPLDPRDFSource memberResIC = new CoAPLDPRDFSource(member, this.getFullName()+"/"+name, mng, memberType);       
        CoAPLDPIndirectContainer ic = new CoAPLDPIndirectContainer(name, this.getFullName(), mng, memberResIC, memberRelation, insertedContentRelation);         
        add(ic);
        mng.setLDPContainsRelationship(mng.getBaseURI() + ic.getFullName(), mng.getBaseURI() + getFullName());
        return ic;
	}

}
