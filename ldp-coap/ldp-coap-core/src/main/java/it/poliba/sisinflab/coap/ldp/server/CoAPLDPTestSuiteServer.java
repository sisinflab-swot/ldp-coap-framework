package it.poliba.sisinflab.coap.ldp.server;

import org.eclipse.californium.core.network.config.NetworkConfig;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPIndirectContainer;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

public class CoAPLDPTestSuiteServer extends CoAPLDPServer {
	
	final static String BASE_URI = "coap://192.168.2.16:5683";
    
    /*
     * Application entry point.
     */
    public static void main(String[] args) {
        
        // create Test Suite server
    	CoAPLDPTestSuiteServer server = new CoAPLDPTestSuiteServer(BASE_URI);
    	server.start();
    }
    
    public CoAPLDPTestSuiteServer(){
    	super(BASE_URI);		
		init();		
    }
    
    public CoAPLDPTestSuiteServer(String URI){
    	super(URI);		
		init();		
    }
    
    public CoAPLDPTestSuiteServer(String URI, NetworkConfig config, int port){
    	super(URI, config, port);		
		init();		
    }
    
    /*
     * Constructor for a new CoAP LDP server. Here, the resources
     * of the server are initialized.
     */
    private void init() {
    	
    	/*** Handle read-only properties ***/
    	setReadOnlyProperty("http://purl.org/dc/terms/created");
    	setConstrainedByURI("http://sisinflab.poliba.it/swottools/ldp-coap/ldp-report.html");
    	
    	/*** Handle not persisted properties ***/
    	setNotPersistedProperty("http://example.com/ns#comment");
        
    	/*** Add LDP-DirectContainer ***/
    	CoAPLDPDirectContainer dc = createDirectContainer("ldp", "resources", LDP.CLASS_RDFSOURCE, LDP.PROP_MEMBER, null);
    	dc.setRDFTitle("Product description of LDP Demo product which is also an LDP-DC");  
      	
      	/*** Add LDP-IndirectContainer in LDP-DirectContainer ***/
    	CoAPLDPIndirectContainer ic = dc.createIndirectContainer("ic", "resource", SSN_XG.System.toString(), SSN_XG.hasSubSystem.toString(), SSN_XG.attachedSystem.toString());
    	ic.setRDFTitle("LDP-CoAP Indirect Container"); 
        ic.getMemberResource().setRDFTitle("LDP-IC Member Resource");
    }
}
