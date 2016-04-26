package it.poliba.sisinflab.coap.ldp.resources;

import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;

import java.io.IOException;
import java.util.HashMap;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPContentFormatException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;

public class CoAPLDPBasicContainer extends CoAPLDPContainer {

	public CoAPLDPBasicContainer(String name, CoAPLDPResourceManager mng) {
		super(name, "", mng);
		this.name = "/" + name;
		initBasicContainer();
	}

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

	@Override
	public void handlePOST(CoapExchange exchange) {
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
					title = getAnonymousResource();
					childName = getURI() + "/" + title;
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

	@Override
	protected void handleLDPPutToCreate(CoapExchange exchange) {
		Request req = exchange.advanced().getCurrentRequest();
		HashMap<String, String> atts = serializeAttributes(req.getOptions().getUriQuery());

		int ct = exchange.getRequestOptions().getContentFormat();
		String title = atts.get(LinkFormat.TITLE);
		String rt = atts.get(LinkFormat.RESOURCE_TYPE);

		if (ct != -1) {
			try {
				String childName = getURI() + "/" + title;

				if (mng.isDeleted(childName)) {
					throw new CoAPLDPException("LDP Resource previously deleted!");
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

	public void handleDELETE(CoapExchange exchange) {
		mng.deleteRDFBasicContainer(mng.getBaseURI() + this.getURI());
		// mng.deleteRDFSource(mng.getBaseURI() + this.getURI());
		this.delete();
		exchange.respond(ResponseCode.DELETED);
	}

	private boolean existChild(String childName) {
		for (Resource r : this.getChildren()) {
			if (r.getURI().equals(childName))
				return true;
		}
		return false;
	}

	private void addNewResource(CoapExchange exchange, int ct, String rt, String childName, String title)
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

	@Override
	public CoAPLDPRDFSource createRDFSource(String name) {
		CoAPLDPRDFSource res = new CoAPLDPRDFSource(name, getFullName(), mng);
		mng.setLDPContainsRelationship(mng.getBaseURI() + res.getFullName(), mng.getBaseURI() + getFullName());
		add(res);
		return res;
	}

	@Override
	public CoAPLDPRDFSource createRDFSource(String name, String type) {
		CoAPLDPRDFSource res = new CoAPLDPRDFSource(name, getFullName(), mng, type);
		mng.setLDPContainsRelationship(mng.getBaseURI() + res.getFullName(), mng.getBaseURI() + getFullName());
		add(res);
		return res;
	}

	@Override
	public CoAPLDPBasicContainer createBasicContainer(String name) {
		CoAPLDPBasicContainer bc = new CoAPLDPBasicContainer(name, this.getFullName(), mng);
		mng.setLDPContainsRelationship(mng.getBaseURI() + bc.getFullName(), mng.getBaseURI() + getFullName());
		add(bc);
		return bc;
	}

	@Override
	public CoAPLDPNonRDFSource createNonRDFSource(String name, int mediaType) {
		CoAPLDPNonRDFSource nr = new CoAPLDPNonRDFSource(name, mng, mediaType);
		mng.setLDPContainsRelationship(mng.getBaseURI() + "/" + nr.getURI(), mng.getBaseURI() + getFullName());
		add(nr);
		return nr;
	}

	@Override
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

	@Override
	public CoAPLDPIndirectContainer createIndirectContainer(String name, String member, String memberType,
			String memberRelation, String insertedContentRelation) {
		CoAPLDPRDFSource memberResIC = new CoAPLDPRDFSource(member, this.getFullName()+"/"+name, mng, memberType);       
        CoAPLDPIndirectContainer ic = new CoAPLDPIndirectContainer(name, this.getFullName(), mng, memberResIC, memberRelation, insertedContentRelation);         
        add(ic);
        mng.setLDPContainsRelationship(mng.getBaseURI() + ic.getFullName(), mng.getBaseURI() + getFullName());
        return ic;
	}

}