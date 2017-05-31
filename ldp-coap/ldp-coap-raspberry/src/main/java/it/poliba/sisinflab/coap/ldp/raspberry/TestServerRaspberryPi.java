package it.poliba.sisinflab.coap.ldp.raspberry;

import it.poliba.sisinflab.coap.ldp.server.CoAPLDPTestSuiteServer;

/**
 * Test Server used to run evaluation on Raspberry Pi board according to the <a href="http://w3c.github.io/ldp-testsuite/">Test Suite for W3C Linked Data Platform 1.0</a> 
 *
 */

public class TestServerRaspberryPi {
	
	final static String BASE_URI = "coap://192.168.2.206:5683";
	final static String BASE_URI_FLAG = "-b";

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

}
