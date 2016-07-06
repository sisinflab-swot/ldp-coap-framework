package it.poliba.sisinflab.coap.ldp.server;

import java.net.SocketException;

import org.eclipse.californium.core.coap.MediaTypeRegistry;

import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.handler.CPUHandler;
import it.poliba.sisinflab.coap.ldp.handler.MemoryHandler;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPIndirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPNonRDFSource;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

/**
 * Test Server used to show main features of LDP-CoAP framework
 *
 */

public class CoAPLDPTestServer {
	
	final static String BASE_URI = "coap://192.168.2.16:5683";
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
    
    /**
	 * Initializes all the resources of the server.
	 * 
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
        
        CoAPLDPRDFSource cpu = bc.createRDFSource("cpu");
        cpu.setDataHandler(new CPUHandler());
        
        CoAPLDPRDFSource memory = bc.createRDFSource("memory", SSN_XG.SensorOutput.toString());
        memory.setDataHandler(new MemoryHandler());
        
        /*** LDP-Resources within a LDP-BasicContainer ***/
        /* CoAPLDPNonRDFSource nrBC = bc.createNonRDFSource("nr-in-bc", MediaTypeRegistry.TEXT_PLAIN);
        nrBC.setData(("Hello World!").getBytes());
        
        bc.createBasicContainer("bc-in-bc");
        bc.createDirectContainer("dc-in-bc", "tempSensor", SSN_XG.SensingDevice.toString(), SSN_XG.observes.toString(), null);
        bc.createIndirectContainer("ic-in-bc", "system", SSN_XG.System.toString(), SSN_XG.hasSubSystem.toString(), SSN_XG.attachedSystem.toString());*/
        
        /*** Add LDP-DirectContainer ***/
        CoAPLDPDirectContainer dc = server.createDirectContainer("obs-dc", "tempSensor", SSN_XG.SensingDevice.toString(), SSN_XG.observes.toString(), null);
        if(dc != null){
        	dc.setRDFTitle("Product description of LDP Demo product which is also an LDP-DC"); 
        	dc.addAcceptPostType(MediaTypeRegistry.TEXT_PLAIN);
        	dc.getMemberResource().setRDFTitle("LDP-DC Resource");
        	
        	CoAPLDPRDFSource resDC = dc.createRDFSource("obs1", SSN_XG.Observation.toString());
        	resDC.setRDFTitle("Product a crashes when shutting down.");
        	
        	/*** LDP-Resources within a LDP-DirectContainer ***/
        	/*CoAPLDPNonRDFSource nrDC = dc.createNonRDFSource("nr-in-dc", MediaTypeRegistry.TEXT_PLAIN);
        	nrDC.setData(("Hello World!").getBytes());
        	
        	dc.createBasicContainer("bc-in-dc");
        	dc.createDirectContainer("dc-in-dc", "tempSensor", SSN_XG.SensingDevice.toString(), SSN_XG.observes.toString(), null);*/
        	
        	CoAPLDPIndirectContainer ic2 = dc.createIndirectContainer("ic-in-dc", "system", SSN_XG.System.toString(), SSN_XG.hasSubSystem.toString(), SSN_XG.attachedSystem.toString());
        	ic2.setRDFTitle("LDP-CoAP IC in DC");
            ic2.getMemberResource().setRDFTitle("LDP-IC Member Resource");
            
            CoAPLDPRDFSource subSystem2 = ic2.createRDFSourceWithDerivedURI("sub-system", SSN_XG.SensorOutput.toString(), BASE_URI + "/sensors/extra/output/out1");
            subSystem2.setRDFTitle("LDP-IC Resource");
        }
        
        
        /*** Add LDP-IndirectContainer ***/
        CoAPLDPIndirectContainer ic = server.createIndirectContainer("obs-ic", "system", SSN_XG.System.toString(), SSN_XG.hasSubSystem.toString(), 
        		SSN_XG.attachedSystem.toString());
        ic.setRDFTitle("LDP-CoAP Indirect Container"); 
        ic.getMemberResource().setRDFTitle("LDP-IC Member Resource");
        
        CoAPLDPRDFSource subSystem = ic.createRDFSourceWithDerivedURI("sub-system", SSN_XG.SensorOutput.toString(), BASE_URI + "/sensors/extra/output/out1");
        subSystem.setRDFTitle("LDP-IC Resource");
        
        /*** LDP-Resources within a LDP-IndirectContainer ***/
        /*CoAPLDPNonRDFSource nrIC = ic.createNonRDFSource("nr-in-ic", MediaTypeRegistry.TEXT_PLAIN);
    	nrIC.setData(("Hello World!").getBytes());
        
        ic.createBasicContainer("bc-in-bc");
        ic.createDirectContainer("dc-in-bc", "tempSensor", SSN_XG.SensingDevice.toString(), SSN_XG.observes.toString(), null);
        ic.createIndirectContainer("ic-in-bc", "system", SSN_XG.System.toString(), SSN_XG.hasSubSystem.toString(), SSN_XG.attachedSystem.toString());*/
    }
}
