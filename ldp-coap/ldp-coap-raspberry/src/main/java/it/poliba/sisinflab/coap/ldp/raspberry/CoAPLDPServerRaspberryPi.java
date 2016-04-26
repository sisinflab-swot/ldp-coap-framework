package it.poliba.sisinflab.coap.ldp.raspberry;

import java.net.SocketException;

import org.eclipse.californium.core.coap.MediaTypeRegistry;

import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPNonRDFSource;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPServer;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;


public class CoAPLDPServerRaspberryPi {
	
	final static String BASE_URI = "coap://192.168.2.206:5683";
    static CoAPLDPServer server;    	
	
    public static void main(String[] args) {        
        // create server
		server = new CoAPLDPServer(BASE_URI);
		
		try {			
			init();
			server.start();
		} catch (SocketException | CoAPLDPException e) {
			e.printStackTrace();
			server.shutdown();
			
			System.out.println("Error during server initialization!");
		}
    }
    
    /*
     * Constructor for a new CoAP LDP server. Here, the resources
     * of the server are initialized.
     */
    public static void init() throws SocketException, CoAPLDPException {
    	
    	server.addHandledNamespace(SSN_XG.PREFIX, SSN_XG.NAMESPACE + "#");
    	
    	/*** Handle read-only properties ***/
    	server.setReadOnlyProperty("http://purl.org/dc/terms/created");
    	server.setConstrainedByURI("http://sisinflab.poliba.it/swottools/ldp-coap/ldp-report.html");
    	
    	/*** Handle not persisted properties ***/
    	server.setNotPersistedProperty("http://example.com/ns#comment");

    	/*** Add LDP-RDFSource ***/  	
    	CoAPLDPRDFSource src = server.createRDFSource("alice");
    	src.setRDFTitle("My first CoAP RDFSource");
    	
    	
    	/*** Add LDP-NonRDFSource ***/
    	CoAPLDPNonRDFSource nr = server.createNonRDFSource("hello", MediaTypeRegistry.TEXT_PLAIN);
    	nr.setData(("Hello World!").getBytes());
    	
    	
    	/*** Add LDP-BasicContainer ***/
        CoAPLDPBasicContainer bc = server.createBasicContainer("sensors");
        bc.setRDFTitle("LDP Basic sensors container");
        bc.setRDFDescription("This container will contain data of sensors."); 
        
        CoAPLDPRDFSource cpuTemp = bc.createRDFSource("cpuTemp");
        cpuTemp.setDataHandler(new CPUTemperatureHandler());     
        
        CoAPLDPRDFSource freeMem = bc.createRDFSource("freeMemory");
        freeMem.setDataHandler(new FreeMemoryHandler());   
        
    }
}
