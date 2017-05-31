package it.poliba.sisinflab.coap.ldp.server;

import org.eclipse.californium.core.network.config.NetworkConfig;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPIndirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPServer;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

/**
 * Test Server used to run evaluation according to the <a href="http://w3c.github.io/ldp-testsuite/">Test Suite for W3C Linked Data Platform 1.0</a> 
 *
 */

public class CoAPLDPTestSuiteServer extends CoAPLDPServer {
	
	final static String BASE_URI = "coap://192.168.2.16:5683";
	final static String BASE_URI_FLAG = "-b";
    
    /*
     * Application entry point.
     */
    public static void main(String[] args) {
    	
    	// create Test Suite server
    	CoAPLDPTestSuiteServer server = null;
    	
    	if (args.length == 2) {
    	    try {
    	    	
    	        if(args[0].equals(BASE_URI_FLAG)){
    	        	server = new CoAPLDPTestSuiteServer(args[1]);  
    	        }
    	        
    	    } catch (Exception e) {
    	    	System.err.println("Error while launching server!");
    	    	System.err.println("[USAGE] java -jar <server>.jar -b <base_uri> ");        
    	    	System.exit(0);
    	    }
    	} else {
    		System.err.println("[WARNINIG] Init parameters not found!");
    		System.err.println("[USAGE] java -jar <server>.jar -b <base_uri> ");
	        System.out.println("Starting Test Suite Server with default base URI: " + BASE_URI);    	        
	        server = new CoAPLDPTestSuiteServer(BASE_URI);  
    	}
    	
    	server.start();
    }
    
    /**
	 * Creates a CoAPLDPTestSuiteServer.
	 * 
	 */
    public CoAPLDPTestSuiteServer(){
    	super(BASE_URI);		
		init();		
    }
    
    /**
	 * Creates a CoAPLDPTestSuiteServer.
	 *
	 * @param  	URI		the repository base URI
	 * 
	 */
    public CoAPLDPTestSuiteServer(String URI){
    	super(URI);		
		init();		
    }
    
    /**
	 * Creates a CoAPLDPTestSuiteServer.
	 *
	 * @param  	baseUri		the repository base URI
	 * @param	config		a custom CoAP network configuration
	 * @param	port		the reference port for the server (default 5683)
	 * 
	 * @see org.eclipse.californium.core.network.config.NetworkConfig
	 * 
	 */
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
    	setReadOnlyProperty("http://purl.org/dc/terms/contributor");
    	setConstrainedByURI("http://sisinflab.poliba.it/swottools/ldp-coap/ldp-report.html");
    	
    	/*** Handle not persisted properties ***/
    	setNotPersistedProperty("http://example.com/ns#comment");  
    	
    	/*** Add LDP-BasicContainer ***/
    	CoAPLDPBasicContainer bc = createBasicContainer("bc");
    	bc.setRDFTitle("LDP-CoAP Basic Container");   	
    	
    	/*** Add LDP-BC as Member ***/
    	CoAPLDPRDFSource bc_res = createRDFSource("bc-asres");    	
    	bc_res.setRDFTitle("LDP-CoAP Basic Container as Member"); 
        
    	/*** Add LDP-DirectContainer ***/
    	CoAPLDPDirectContainer dc = createDirectContainer("dc-simple", "resources", LDP.CLASS_RDFSOURCE, LDP.PROP_MEMBER, null);
    	dc.setRDFTitle("Product description of LDP Demo product which is also an LDP-DC");       	
      	
      	/*** Add LDP-IndirectContainer ***/
    	CoAPLDPIndirectContainer ic = createIndirectContainer("ic", "resource", SSN_XG.System.toString(), SSN_XG.hasSubSystem.toString(), SSN_XG.attachedSystem.toString());
    	ic.setRDFTitle("LDP-CoAP Indirect Container"); 
        ic.getMemberResource().setRDFTitle("LDP-IC Member Resource");
    }
}
