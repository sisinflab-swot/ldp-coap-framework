package it.poliba.sisinflab.coap.ldp;

import static org.junit.Assert.assertEquals;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.junit.BeforeClass;
import org.junit.Test;

import it.poliba.sisinflab.coap.ldp.server.CoAPLDPTestServer;

public class BasicTest {
	
	final static String BASE_URI = "coap://192.168.2.16:5683";	
	static CoAPLDPTestServer server;

    @BeforeClass
    public static void runOnceBeforeClass() {
        server = new CoAPLDPTestServer(BASE_URI);		
		server.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getLDPRDFSource() {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.TEXT_TURTLE);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
}
