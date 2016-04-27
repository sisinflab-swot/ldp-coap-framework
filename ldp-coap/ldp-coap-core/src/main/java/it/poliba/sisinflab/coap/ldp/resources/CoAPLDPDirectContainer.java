package it.poliba.sisinflab.coap.ldp.resources;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPContentFormatException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;

public class CoAPLDPDirectContainer extends CoAPLDPContainer {

	CoAPLDPRDFSource resource;
	String memberRelation;
	String isMemberOfRelation;
	boolean directRel = true;

	public CoAPLDPDirectContainer(String name, CoAPLDPResourceManager mng, CoAPLDPRDFSource resource,
			String memberRelation, String isMemberOfRelation) throws CoAPLDPException {
		super(name, "", mng);
		this.memberRelation = memberRelation;
		this.isMemberOfRelation = isMemberOfRelation;

		if (this.memberRelation == null) {
			if (this.isMemberOfRelation != null)
				directRel = false;
			else
				throw new CoAPLDPException("LDP Membership relation not found!");
		}

		this.name = "/" + name;

		init();
		addMembershipResource(resource);
	}

	public CoAPLDPDirectContainer(String name, String path, CoAPLDPResourceManager mng, CoAPLDPRDFSource resource,
			String memberRelation, String isMemberOfRelation) throws CoAPLDPException {
		super(name, path, mng);
		this.memberRelation = memberRelation;
		this.isMemberOfRelation = isMemberOfRelation;

		if (this.memberRelation == null) {
			if (this.isMemberOfRelation != null)
				directRel = false;
			else
				throw new CoAPLDPException("LDP Membership relation not found!");
		}

		this.name = path + "/" + getURI();

		init();
		addMembershipResource(resource);
	}

	private void init() {
		this.fRDFType = LDP.CLASS_DIRECT_CONTAINER;

		getAttributes().addResourceType(LDP.CLASS_DIRECT_CONTAINER);

		mng.addRDFDirectContainer(mng.getBaseURI() + name);

		options.setAllowedMethod(LDP.Code.POST, true);

		options.addAcceptPostType(MediaTypeRegistry.TEXT_TURTLE);
		options.addAcceptPostType(MediaTypeRegistry.APPLICATION_LD_JSON);
	}
	
	@Override
	protected void handleLDPPutToCreate(CoapExchange exchange) {
		this.postResource(exchange, true);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		this.postResource(exchange, false);
	}
	
	private void postResource(CoapExchange exchange, boolean putToCreate){
		Request req = exchange.advanced().getCurrentRequest();
		HashMap<String, String> atts = serializeAttributes(req.getOptions().getUriQuery());

		String title = atts.get(LinkFormat.TITLE);
		if (title == null) {
			title = getAnonymousResource();
		}

		int ct = exchange.getRequestOptions().getContentFormat();

		if ((ct != -1) && (title != null)) {

			String body = exchange.getRequestText();
			try {
				String childName = resource.getURI() + "/" + title;

				if (mng.isDeleted(childName)) {
					if(!putToCreate){
						title = getAnonymousResource();
						childName = resource.getURI() + "/" + title;
					} else {
						throw new CoAPLDPException("LDP Resource previously deleted!");
					}
				}

				if (!existChild(childName)) {

					if (ct == MediaTypeRegistry.TEXT_TURTLE || ct == MediaTypeRegistry.APPLICATION_LD_JSON) {

						RDFFormat f;
						if (ct == MediaTypeRegistry.TEXT_TURTLE)
							f = RDFFormat.TURTLE;
						else
							f = RDFFormat.JSONLD;

						mng.postRDFSource(mng.getBaseURI() + childName, body, f);

						String rt = atts.get(LinkFormat.RESOURCE_TYPE);
						if ((rt == null) || (rt.equals(LDP.LINK_LDP + ":" + LDP.CLASS_LNAME_RESOURCE))) {
							/*** Add LDP-RDFSource ***/
							CoAPLDPRDFSource s = new CoAPLDPRDFSource(title, resource.getURI(), mng);
							this.addRDFResource(s);
						} else if (rt.equals(LDP.LINK_LDP + ":" + LDP.CLASS_LNAME_BASIC_CONTAINER)) {
							/*** Add LDP-BasicContainer ***/
							CoAPLDPBasicContainer bc = new CoAPLDPBasicContainer(title, mng);
							bc.setRDFCreated();
							this.addRDFResource(bc);
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
							CoAPLDPDirectContainer dc = new CoAPLDPDirectContainer(title, resource.getFullName(), mng,
									memberRes, memberRel, isMemberOfRel);
							dc.setRDFCreated();
							this.addRDFResource(dc);
						} else
							throw new CoAPLDPException("Invalid RT query parameter.");
					} else if (options.getAcceptedPostTypes().contains(ct)) {
						/*** Add LDP-NonRDFSource ***/
						CoAPLDPNonRDFSource nRDF = new CoAPLDPNonRDFSource(title, mng, ct);
						nRDF.setData(exchange.getRequestPayload());
						this.addRDFResource(nRDF);
					} else
						throw new CoAPLDPContentFormatException("Content-Format (CT) Not Accepted.");

					exchange.setLocationPath(childName);
					exchange.setLocationQuery(
							LinkFormat.RESOURCE_TYPE + "=" + LDP.LINK_LDP + ":" + LDP.CLASS_LNAME_RESOURCE);
					exchange.respond(ResponseCode.CREATED);
				} else
					exchange.respond(ResponseCode.FORBIDDEN);

			} catch (CoAPLDPContentFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exchange.respond(ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
			} catch (RDFParseException | CoAPLDPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exchange.respond(ResponseCode.BAD_REQUEST);
			} catch (RepositoryException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
			}
		} else {
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}

	public void handleDELETE(CoapExchange exchange) {
		mng.deleteRDFDirectContainer(mng.getBaseURI() + this.getURI());
		// mng.deleteRDFSource(mng.getBaseURI() + this.getURI());
		this.delete();
		exchange.respond(ResponseCode.DELETED);
	}

	private boolean existChild(String childName) {
		for (Resource r : resource.getChildren()) {
			if (r.getURI().equals(childName))
				return true;
		}
		return false;
	}

	private void addMembershipResource(CoAPLDPRDFSource resource) {
		this.resource = resource;
		add(resource);
		mng.setLDPMembershipResource(mng.getBaseURI() + resource.getFullName(), mng.getBaseURI() + name);

		if (directRel)
			mng.setLDPMemberRelation(mng.getBaseURI() + name, this.memberRelation);
		else
			mng.setLDPInverseMemberRelation(mng.getBaseURI() + name, this.isMemberOfRelation);
	}

	private void addRDFResource(CoAPLDPResource res) {
		resource.add(res);

		String r = checkURI(res.getFullName());
		String m = checkURI(resource.getFullName());
		String c = checkURI(name);

		mng.setLDPContainsRelationship(mng.getBaseURI() + "/" + r, mng.getBaseURI() + "/" + c);

		if (directRel)
			mng.setRDFStatement(mng.getBaseURI() + "/" + m, this.memberRelation, mng.getBaseURI() + "/" + r);
		else
			mng.setRDFStatement(mng.getBaseURI() + "/" + r, this.isMemberOfRelation, mng.getBaseURI() + "/" + m);
	}

	private String checkURI(String s) {
		if (s.startsWith("/"))
			return s.substring(1);
		else
			return s;
	}

	public CoAPLDPRDFSource getMemberResource() {
		return this.resource;
	}

	@Override
	public CoAPLDPRDFSource createRDFSource(String name, String type) {
		CoAPLDPRDFSource res = new CoAPLDPRDFSource(name, resource.getFullName(), mng, type);
		this.addRDFResource(res);
		return res;
	}

	@Override
	public CoAPLDPRDFSource createRDFSource(String name) {
		CoAPLDPRDFSource res = new CoAPLDPRDFSource(name, resource.getFullName(), mng);
		this.addRDFResource(res);
		return res;
	}

	@Override
	public CoAPLDPBasicContainer createBasicContainer(String name) {
		CoAPLDPBasicContainer bc = new CoAPLDPBasicContainer(name, resource.getFullName(), mng);
		this.addRDFResource(bc);
		return bc;
	}

	@Override
	public CoAPLDPNonRDFSource createNonRDFSource(String name, int mediaType) {
		CoAPLDPNonRDFSource nr = new CoAPLDPNonRDFSource(name, mng, mediaType);
		this.addRDFResource(nr);
		return nr;
	}

	@Override
	public CoAPLDPIndirectContainer createIndirectContainer(String name, String member, String memberType,
			String memberRelation, String insertedContentRelation) {
		CoAPLDPRDFSource memberResIC = new CoAPLDPRDFSource(member, resource.getFullName()+"/"+name, mng, memberType);       
        CoAPLDPIndirectContainer ic = new CoAPLDPIndirectContainer(name, resource.getFullName(), mng, memberResIC, memberRelation, insertedContentRelation);   
        this.addRDFResource(ic);
		return ic;
	}

	@Override
	public CoAPLDPDirectContainer createDirectContainer(String name, String member, String memberType,
			String memberRelation, String isMemberOfRelation) {
		CoAPLDPRDFSource memberRes = new CoAPLDPRDFSource(member, resource.getFullName() + "/" + name, mng, memberType);
		CoAPLDPDirectContainer dc = null;
		try {
			dc = new CoAPLDPDirectContainer(name, resource.getFullName(), mng, memberRes, memberRelation,
					isMemberOfRelation);
			this.addRDFResource(dc);
		} catch (CoAPLDPException e) {
			e.printStackTrace();
		}
		return dc;
	}

}
