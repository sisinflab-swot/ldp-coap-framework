package it.poliba.sisinflab.coap.ldp.resources;

import java.util.HashMap;
import java.util.List;

import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.LDP.Code;

public abstract class CoAPLDPContainer extends CoAPLDPRDFSource {

	protected int anonymous = 0;

	public CoAPLDPContainer(String name, String path, CoAPLDPResourceManager mng) {
		super(name, path, mng);

		this.fRDFType = LDP.CLASS_CONTAINER;
		getAttributes().addResourceType(LDP.CLASS_CONTAINER);

		mng.addRDFContainer(mng.getBaseURI() + this.name);
	}

	public void setAcceptPostType(int ct) {
		options.addAcceptPostType(ct);
	}

	protected String getAnonymousResource() {
		this.anonymous++;
		return "res" + this.anonymous;
	}

	@Override
	public void handlePUT(CoapExchange exchange) {

		Code m = getLDPMethod(exchange);

		if (m.equals(LDP.Code.PUT)) {
			String title = findTitle(exchange);
			if (title == null)
				handleLDPPUT(exchange);
			else
				handleLDPPutToCreate(exchange);
		} else if (m.equals(LDP.Code.PATCH)) {
			handleLDPPATCH(exchange);
		}
	}

	protected void handleLDPPutToCreate(CoapExchange exchange) {
		exchange.respond(ResponseCode.METHOD_NOT_ALLOWED);
	}

	private String findTitle(CoapExchange exchange) {
		List<String> q = exchange.getRequestOptions().getUriQuery();
		HashMap<String, String> atts = serializeAttributes(q);
		return atts.get(LinkFormat.TITLE);
	}

	public abstract CoAPLDPRDFSource createRDFSource(String name);

	public abstract CoAPLDPRDFSource createRDFSource(String name, String type);

	public abstract CoAPLDPNonRDFSource createNonRDFSource(String name, int mediaType);

	public abstract CoAPLDPBasicContainer createBasicContainer(String name);

	public abstract CoAPLDPDirectContainer createDirectContainer(String name, String member, String memberType,
			String memberRelation, String isMemberOfRelation);

	public abstract CoAPLDPIndirectContainer createIndirectContainer(String name, String member, String memberType,
			String memberRelation, String insertedContentRelation);

}
