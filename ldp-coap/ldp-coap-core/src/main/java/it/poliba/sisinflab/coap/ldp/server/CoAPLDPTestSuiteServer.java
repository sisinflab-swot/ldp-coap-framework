package it.poliba.sisinflab.coap.ldp.server;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPIndirectContainer;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

public class CoAPLDPTestSuiteServer {
	
	final static String BASE_URI = "coap://192.168.2.16:5683";
	static CoAPLDPServer server;
    
    /*
     * Application entry point.
     */
    public static void main(String[] args) {
        
        // create server
		server = new CoAPLDPServer(BASE_URI);
		
		init();
		
		server.start();
    }
    
    /*
     * Constructor for a new CoAP LDP server. Here, the resources
     * of the server are initialized.
     */
    public static void init() {
    	
    	/*** Handle read-only properties ***/
    	server.setReadOnlyProperty("http://purl.org/dc/terms/created");
    	server.setConstrainedByURI("http://sisinflab.poliba.it/swottools/ldp-coap/ldp-report.html");
    	
    	/*** Handle not persisted properties ***/
    	server.setNotPersistedProperty("http://example.com/ns#comment");
        
    	/*** Add LDP-DirectContainer ***/
    	CoAPLDPDirectContainer dc = server.createDirectContainer("ldp", "resources", LDP.CLASS_RDFSOURCE, LDP.PROP_MEMBER, null);
    	dc.setRDFTitle("Product description of LDP Demo product which is also an LDP-DC");  
      	
      	/*** Add LDP-IndirectContainer in LDP-DirectContainer ***/
    	CoAPLDPIndirectContainer ic = dc.createIndirectContainer("ic", "resource", SSN_XG.System.toString(), SSN_XG.hasSubSystem.toString(), SSN_XG.attachedSystem.toString());
    	ic.setRDFTitle("LDP-CoAP Indirect Container"); 
        ic.getMemberResource().setRDFTitle("LDP-IC Member Resource");
    }
}
