package it.poliba.sisinflab.coap.ldp.resources;

import java.util.HashMap;
import java.util.List;

import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.LDP.Code;

/**
 * Represents an LDP Container
 * <p> 
 * @see <a href="https://www.w3.org/TR/ldp/#ldpc-container">#LDP Container</a>
 *
 */

public abstract class CoAPLDPContainer extends CoAPLDPRDFSource {

	protected int anonymous = 0;

	/**
	 * Creates a new LDP Container.
	 *
	 * @param  	name 	the name of the container
	 * @param	path	the path of the root resource (if present)
	 * @param	mng		the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */	
	public CoAPLDPContainer(String name, String path, CoAPLDPResourceManager mng) {
		super(name, path, mng);

		this.fRDFType = LDP.CLASS_CONTAINER;
		getAttributes().addResourceType(LDP.CLASS_CONTAINER);

		mng.addRDFContainer(mng.getBaseURI() + this.name);
	}

	/**
	 * Adds a content-type accepted by the container for POST requests.
	 *
	 * @param  	ct	the accepted content-type value as defined in MediaTypeRegistry
	 * 
	 * @see org.eclipse.californium.core.coap.MediaTypeRegistry
	 */	
	public void addAcceptPostType(int ct) {
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

	/**
	 * Creates a new LDP RDF Source as contained object.
	 *
	 * @param  name 	the name of the contained resource
	 * 
	 * @return CoAPLDPRDFSource		the created resource
	 * 
	 */
	public abstract CoAPLDPRDFSource createRDFSource(String name);

	/**
	 * Creates a new LDP RDF Source as contained object.
	 *
	 * @param  	name 	the name of the contained resource
	 * @param	type	the type of the contained resource
	 * 
	 * @return CoAPLDPRDFSource		the created resource
	 */
	public abstract CoAPLDPRDFSource createRDFSource(String name, String type);

	/**
	 * Creates a new LDP Non-RDF Source as contained object.
	 *
	 * @param  	name 		the name of the contained resource
	 * @param	mediaType	the content-type value of the contained resource as defined in MediaTypeRegistry
	 * 
	 * @return 	CoAPLDPNonRDFSource		the created resource
	 * 
	 * @see org.eclipse.californium.core.coap.MediaTypeRegistry
	 */
	public abstract CoAPLDPNonRDFSource createNonRDFSource(String name, int mediaType);

	/**
	 * Creates a new LDP Basic Container as contained object.
	 *
	 * @param  	name 	the name of the contained resource
	 * 
	 * @return 	CoAPLDPBasicContainer		the created resource
	 */
	public abstract CoAPLDPBasicContainer createBasicContainer(String name);

	/**
	 * Creates a new LDP Direct Container as contained object.
	 *
	 * @param  	name 				the name of the contained resource
	 * @param  	member 				the name of the member resource of the created Direct Container
	 * @param  	memberType 			the type of the member resource of the created Direct Container
	 * @param  	memberRelation 		the memberRelation property of the created Direct Container (if present)
	 * @param  	isMemberOfRelation 	the isMemberOfRelation property of the created Direct Container (if present)
	 * 
	 * @return 	CoAPLDPDirectContainer		the created resource
	 */
	public abstract CoAPLDPDirectContainer createDirectContainer(String name, String member, String memberType,
			String memberRelation, String isMemberOfRelation);

	/**
	 * Creates a new LDP Indirect Container as contained object.
	 *
	 * @param  	name 						the name of the contained resource
	 * @param  	member 						the name of the member resource of the created Indirect Container
	 * @param  	memberType 					the type of the member resource of the created Indirect Container
	 * @param  	memberRelation 				the memberRelation property of the created Indirect Container
	 * @param  	insertedContentRelation 	the insertedContentRelation property of the created Indirect Container
	 * 
	 * @return 	CoAPLDPDirectContainer		the created resource
	 */
	public abstract CoAPLDPIndirectContainer createIndirectContainer(String name, String member, String memberType,
			String memberRelation, String insertedContentRelation);
	
}
