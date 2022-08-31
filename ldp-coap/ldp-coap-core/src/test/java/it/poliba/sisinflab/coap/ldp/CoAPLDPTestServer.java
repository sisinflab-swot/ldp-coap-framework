package it.poliba.sisinflab.coap.ldp;

import java.net.SocketException;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;

import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.handler.CPUHandler;
import it.poliba.sisinflab.coap.ldp.handler.MemoryHandler;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPIndirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPNonRDFSource;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPServer;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

/**
 * Test Server used to show main features of LDP-CoAP framework
 *
 */

public class CoAPLDPTestServer extends CoAPLDPServer {

	final static String BASE_URI = "coap://127.0.0.1:5683";	
	
    public static void main(String[] args) throws SocketException {        
        // create server
    	CoAPLDPTestServer server = new CoAPLDPTestServer(BASE_URI);		
		server.start();
    }
    
    public CoAPLDPTestServer(String BASE_URI) {
		super(BASE_URI);
		try {
			init();
		} catch (SocketException | CoAPLDPException e) {
			e.printStackTrace();
			System.out.println("Error during server initialization!");
			System.exit(0);
		}
	}
    
    /**
	 * Initializes all the resources of the server.
	 * 
	 */
    public void init() throws SocketException, CoAPLDPException {
    	
    	addHandledNamespace(SSN_XG.PREFIX, SSN_XG.NAMESPACE + "#");
    	
    	/*** Handle read-only properties ***/
    	setReadOnlyProperty(DCTERMS.CREATED.stringValue());
    	setConstrainedByURI("http://sisinflab.poliba.it/swottools/ldp-coap/ldp-report.html");
    	
    	/*** Handle not persisted properties ***/
    	setNotPersistedProperty("http://example.com/ns#comment");

    	/*** Add LDP-RDFSource ***/  	
    	CoAPLDPRDFSource src = createRDFSource("alice");
    	src.setRDFTitle("My first CoAP RDFSource");
    	
    	
    	/*** Add LDP-NonRDFSource ***/
    	CoAPLDPNonRDFSource nr = createNonRDFSource("hello", MediaTypeRegistry.TEXT_PLAIN);
    	nr.setData(("Hello World!").getBytes());
    	
    	
    	/*** Add LDP-BasicContainer ***/
        CoAPLDPBasicContainer bc = createBasicContainer("sensors");
        bc.setRDFTitle("LDP Basic sensors container");
        bc.setRDFDescription("This container will contain data of sensors."); 
        
        CoAPLDPRDFSource cpu = bc.createRDFSource("cpu");
        cpu.setDataHandler(new CPUHandler());
        
        CoAPLDPRDFSource memory = bc.createRDFSource("memory", SSN_XG.SensorOutput.stringValue());
        memory.setDataHandler(new MemoryHandler());
        
        /*** LDP-Resources within a LDP-BasicContainer ***/
        /* CoAPLDPNonRDFSource nrBC = bc.createNonRDFSource("nr-in-bc", MediaTypeRegistry.TEXT_PLAIN);
        nrBC.setData(("Hello World!").getBytes());
        
        bc.createBasicContainer("bc-in-bc");
        bc.createDirectContainer("dc-in-bc", "tempSensor", SSN_XG.SensingDevice.stringValue(), SSN_XG.observes.stringValue(), null);
        bc.createIndirectContainer("ic-in-bc", "system", SSN_XG.System.stringValue(), SSN_XG.hasSubSystem.stringValue(), SSN_XG.attachedSystem.stringValue());*/
        
        /*** Add LDP-DirectContainer ***/
        CoAPLDPDirectContainer dc = createDirectContainer("obs-dc", "tempSensor", SSN_XG.SensingDevice.stringValue(), SSN_XG.observes.stringValue(), null);
        if(dc != null){
        	dc.setRDFTitle("Product description of LDP Demo product which is also an LDP-DC"); 
        	dc.addAcceptPostType(MediaTypeRegistry.TEXT_PLAIN);
        	dc.getMemberResource().setRDFTitle("LDP-DC Resource");
        	
        	CoAPLDPRDFSource resDC = dc.createRDFSource("obs1", SSN_XG.Observation.stringValue());
        	resDC.setRDFTitle("Product a crashes when shutting down.");
        	
        	/*** LDP-Resources within a LDP-DirectContainer ***/
        	/*CoAPLDPNonRDFSource nrDC = dc.createNonRDFSource("nr-in-dc", MediaTypeRegistry.TEXT_PLAIN);
        	nrDC.setData(("Hello World!").getBytes());
        	
        	dc.createBasicContainer("bc-in-dc");
        	dc.createDirectContainer("dc-in-dc", "tempSensor", SSN_XG.SensingDevice.stringValue(), SSN_XG.observes.stringValue(), null);*/
        	
        	CoAPLDPIndirectContainer ic2 = dc.createIndirectContainer("ic-in-dc", "system", SSN_XG.System.stringValue(), SSN_XG.hasSubSystem.stringValue(), SSN_XG.attachedSystem.stringValue());
        	ic2.setRDFTitle("LDP-CoAP IC in DC");
            ic2.getMemberResource().setRDFTitle("LDP-IC Member Resource");
            
            CoAPLDPRDFSource subSystem2 = ic2.createRDFSourceWithDerivedURI("sub-system", SSN_XG.SensorOutput.stringValue(), BASE_URI + "/sensors/extra/output/out1");
            subSystem2.setRDFTitle("LDP-IC Resource");
        }
        
        
        /*** Add LDP-IndirectContainer ***/
        CoAPLDPIndirectContainer ic = createIndirectContainer("obs-ic", "system", SSN_XG.System.stringValue(), SSN_XG.hasSubSystem.stringValue(), 
        		SSN_XG.attachedSystem.stringValue());
        ic.setRDFTitle("LDP-CoAP Indirect Container"); 
        ic.getMemberResource().setRDFTitle("LDP-IC Member Resource");
        
        CoAPLDPRDFSource subSystem = ic.createRDFSourceWithDerivedURI("sub-system", SSN_XG.SensorOutput.stringValue(), BASE_URI + "/sensors/extra/output/out1");
        subSystem.setRDFTitle("LDP-IC Resource");
        
        /*** LDP-Resources within a LDP-IndirectContainer ***/
        /*CoAPLDPNonRDFSource nrIC = ic.createNonRDFSource("nr-in-ic", MediaTypeRegistry.TEXT_PLAIN);
    	nrIC.setData(("Hello World!").getBytes());
        
        ic.createBasicContainer("bc-in-bc");
        ic.createDirectContainer("dc-in-bc", "tempSensor", SSN_XG.SensingDevice.stringValue(), SSN_XG.observes.stringValue(), null);
        ic.createIndirectContainer("ic-in-bc", "system", SSN_XG.System.stringValue(), SSN_XG.hasSubSystem.stringValue(), SSN_XG.attachedSystem.stringValue());*/
    }
}
