package it.poliba.sisinflab.coap.ldp.server;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.Resource;

import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPIndirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPNonRDFSource;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPResourceManager;

/**
 * Implements a CoAP server supporting all LDP-CoAP features
 *
 */

public class CoAPLDPServer extends CoapServer {
	
	CoAPLDPResourceManager mng;
	CoAPLDPServerMessageDeliverer smd;
	String BASE_URI;
	
	@Deprecated 
	public CoapServer add(Resource... resources) {return super.add(resources);}
	
	/**
	 * Creates a new LDP-CoAP server.
	 * 
	 * @param BASE_URI	the base URI for the RDF repository
	 * 
	 */
    public CoAPLDPServer(String BASE_URI) {
    	super();    	    	
    	
    	this.BASE_URI = BASE_URI;    	
    	mng = new CoAPLDPResourceManager(BASE_URI);
    	
    	smd = new CoAPLDPServerMessageDeliverer(this.getRoot());    	
    	this.setMessageDeliverer(smd);
    	
    	printWelcome();
    }
    
    /**
	 * Creates a new LDP-CoAP server.
	 * 
	 * @param 	BASE_URI	the repository base URI
	 * @param	config		a custom CoAP network configuration
	 * @param	port		the reference port for the server (default 5683)
	 * 
	 */
    public CoAPLDPServer(String BASE_URI, NetworkConfig config, int port) {
    	super(config, port);
    	
    	this.BASE_URI = BASE_URI;    	
    	mng = new CoAPLDPResourceManager(BASE_URI);
    	
    	CoAPLDPServerMessageDeliverer smd = new CoAPLDPServerMessageDeliverer(this.getRoot());    	
    	this.setMessageDeliverer(smd);
    	
    	printWelcome();
    }
    
    private void printWelcome(){
    	System.out.println("----------------------------------------------------------");
    	System.out.println("### LDP-CoAP Server ###");
    	System.out.println("Version: 1.1.0");
    	System.out.println("Endpoint: " + BASE_URI);
    	System.out.println("Developed by: SisInfLab - Politecnico di Bari");
    	System.out.println("Home Page: http://sisinflab.poliba.it/swottools/ldp-coap/");
    	System.out.println("----------------------------------------------------------");
    }
    
    /**
	 * Stops the LDP-CoAP server.
	 * 
	 */
    public void shutdown(){
    	
    	stopResourceHandler(this.getRoot());
    	System.out.println("Resource Handlers stopped!");
    	
        mng.close();
        this.stop();
    }
    
    private void stopResourceHandler(Resource r){    	
    	if(r instanceof CoAPLDPRDFSource){
    		((CoAPLDPRDFSource) r).stopPublishData();
    	}
    	for(Resource child : r.getChildren()){
    		this.stopResourceHandler(child);
    	}    	
    }
    
    /**
	 * Adds a well-known namespace
	 *
	 * @param  	prefix		the namespace prefix
	 * @param	namespace	the namespace URI
	 * 
	 */
    public void addHandledNamespace(String prefix, String namespace){
    	mng.addHandledNamespace(prefix, namespace);
    }
    
    /**
	 * Adds a read-only property constraint
	 * 
	 * @param  	uri	the URI of the read-only property
	 * 
	 */
    public void setReadOnlyProperty(String uri){
    	mng.addReadOnlyProperty(uri);
    }
    
    /**
	 * Sets the LDP constrainedBy URI
	 * 
	 * @param  	uri		the URI of the LDP constrainedBy property
	 * 
	 */
    public void setConstrainedByURI(String uri){
    	mng.setConstrainedByURI(uri);
    }
    
    /**
	 * Adds a not-persisted property constraint
	 * 
	 * @param  	uri	the URI of the not-persisted property
	 * 
	 */
    public void setNotPersistedProperty(String uri){
    	mng.addNotPersistedProperty(uri);
    }
    
    /**
	 * Creates a new LDP RDF Source.
	 *
	 * @param  name 	the name of the contained resource
	 * 
	 * @return CoAPLDPRDFSource		the created resource
	 * 
	 */
    public CoAPLDPRDFSource createRDFSource(String name){
    	CoAPLDPRDFSource src = new CoAPLDPRDFSource(name, mng);    	
    	src.setRDFCreated();
    	add(src);    	
    	return src;
    }
    
    /**
	 * Creates a new LDP RDF Source.
	 *
	 * @param  	name 	the name of the contained resource
	 * @param	type	the type of the contained resource
	 * 
	 * @return CoAPLDPRDFSource		the created resource
	 */
    public CoAPLDPRDFSource createRDFSource(String name, String type){
    	CoAPLDPRDFSource src = new CoAPLDPRDFSource(name, mng, type);    	
    	src.setRDFCreated();
    	add(src);    	
    	return src;
    }
    
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
    public CoAPLDPNonRDFSource createNonRDFSource(String name, int mediaType){
    	CoAPLDPNonRDFSource src = new CoAPLDPNonRDFSource(name, mng, mediaType);    	
    	add(src);    	
    	return src;
    }
    
    /**
	 * Creates a new LDP Basic Container as contained object.
	 *
	 * @param  	container 	the name of the contained resource
	 * 
	 * @return 	CoAPLDPBasicContainer		the created resource
	 */
    public CoAPLDPBasicContainer createBasicContainer(String container){
    	CoAPLDPBasicContainer bc = new CoAPLDPBasicContainer(container, mng);     
        bc.setRDFCreated();
        add(bc); 
        return bc;
    }
    
    /**
	 * Creates a new LDP Direct Container as contained object.
	 *
	 * @param  	container			the name of the contained resource
	 * @param  	member 				the name of the member resource of the created Direct Container
	 * @param  	memberType 			the type of the member resource of the created Direct Container
	 * @param  	memberRelation 		the memberRelation property of the created Direct Container (if present)
	 * @param  	isMemberOfRelation 	the isMemberOfRelation property of the created Direct Container (if present)
	 * 
	 * @return 	CoAPLDPDirectContainer		the created resource
	 */
    public CoAPLDPDirectContainer createDirectContainer(String container, String member, String memberType, String memberRelation, String isMemberOfRelation){
        CoAPLDPRDFSource memberRes = new CoAPLDPRDFSource(member, "/"+container, mng, memberType);        
        CoAPLDPDirectContainer dc = null;
		try {
			dc = new CoAPLDPDirectContainer(container, mng, memberRes, memberRelation, isMemberOfRelation);
			dc.setRDFCreated();	        
	        add(dc);	        
		} catch (CoAPLDPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return dc;
    }
    
    /**
	 * Creates a new LDP Indirect Container as contained object.
	 *
	 * @param  	container					the name of the contained resource
	 * @param  	member 						the name of the member resource of the created Indirect Container
	 * @param  	memberType 					the type of the member resource of the created Indirect Container
	 * @param  	memberRelation 				the memberRelation property of the created Indirect Container
	 * @param  	insertedContentRelation 	the insertedContentRelation property of the created Indirect Container
	 * 
	 * @return 	CoAPLDPDirectContainer		the created resource
	 */
    public CoAPLDPIndirectContainer createIndirectContainer(String container, String member, String memberType, String memberRelation, String insertedContentRelation){
    	CoAPLDPRDFSource memberResIC = new CoAPLDPRDFSource(member, "/"+container, mng, memberType);       
        CoAPLDPIndirectContainer ic = new CoAPLDPIndirectContainer(container, mng, memberResIC, memberRelation, insertedContentRelation);  
        ic.setRDFCreated();       
        add(ic);
        return ic;
    }
    
    /**
	 * Returns the resource manager.
	 * 
	 * @return 	CoAPLDPResourceManager		the default resource manager
	 */
    public CoAPLDPResourceManager getCoAPLDPResourceManager(){
    	return this.mng;
    }

}
