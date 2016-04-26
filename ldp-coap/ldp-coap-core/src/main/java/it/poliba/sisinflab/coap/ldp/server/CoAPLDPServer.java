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

public class CoAPLDPServer extends CoapServer {
	
	CoAPLDPResourceManager mng;
	String BASE_URI;
	
	@Deprecated 
	public CoapServer add(Resource... resources) {return super.add(resources);}
	
	/*
     * Constructor for a new CoAP LDP server. Here, the resources
     * of the server are initialized.
     */
    public CoAPLDPServer(String BASE_URI) {
    	super();
    	
    	this.BASE_URI = BASE_URI;    	
    	mng = new CoAPLDPResourceManager(BASE_URI);
    	
    	CoAPLDPServerMessageDeliverer smd = new CoAPLDPServerMessageDeliverer(this.getRoot());    	
    	this.setMessageDeliverer(smd);
    }
    
    public CoAPLDPServer(String BASE_URI, NetworkConfig config, int port) {
    	super(config, port);
    	
    	this.BASE_URI = BASE_URI;    	
    	mng = new CoAPLDPResourceManager(BASE_URI);
    	
    	CoAPLDPServerMessageDeliverer smd = new CoAPLDPServerMessageDeliverer(this.getRoot());    	
    	this.setMessageDeliverer(smd);
    }
    
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
    
    public void addHandledNamespace(String prefix, String namespace){
    	mng.addHandledNamespace(prefix, namespace);
    }
    
    public void setReadOnlyProperty(String uri){
    	mng.setReadOnlyProperty(uri);
    }
    
    public void setConstrainedByURI(String uri){
    	mng.setConstrainedByURI(uri);
    }
    
    public void setNotPersistedProperty(String uri){
    	mng.setNotPersistedProperty(uri);
    }
    
    public CoAPLDPRDFSource createRDFSource(String name){
    	CoAPLDPRDFSource src = new CoAPLDPRDFSource(name, mng);    	
    	src.setRDFCreated();
    	add(src);    	
    	return src;
    }
    
    public CoAPLDPRDFSource createRDFSource(String name, String type){
    	CoAPLDPRDFSource src = new CoAPLDPRDFSource(name, mng, type);    	
    	src.setRDFCreated();
    	add(src);    	
    	return src;
    }
    
    public CoAPLDPNonRDFSource createNonRDFSource(String name, int mediaType){
    	CoAPLDPNonRDFSource src = new CoAPLDPNonRDFSource(name, mng, mediaType);    	
    	add(src);    	
    	return src;
    }
    
    public CoAPLDPBasicContainer createBasicContainer(String container){
    	CoAPLDPBasicContainer bc = new CoAPLDPBasicContainer(container, mng);     
        bc.setRDFCreated();
        add(bc); 
        return bc;
    }
    
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
    
    public CoAPLDPIndirectContainer createIndirectContainer(String container, String member, String memberType, String memberRelation, String insertedContentRelation){
    	CoAPLDPRDFSource memberResIC = new CoAPLDPRDFSource(member, "/"+container, mng, memberType);       
        CoAPLDPIndirectContainer ic = new CoAPLDPIndirectContainer(container, mng, memberResIC, memberRelation, insertedContentRelation);  
        ic.setRDFCreated();       
        add(ic);
        return ic;
    }
    
    public CoAPLDPResourceManager getCoAPLDPResourceManager(){
    	return this.mng;
    }

}
