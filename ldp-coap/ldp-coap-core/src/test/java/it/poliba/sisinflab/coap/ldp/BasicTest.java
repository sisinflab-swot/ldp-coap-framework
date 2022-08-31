package it.poliba.sisinflab.coap.ldp;

import static org.junit.Assert.assertEquals;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicTest {
	
	final static String BASE_URI = "coap://127.0.0.1:5683";	
	static CoAPLDPTestServer server;

    @BeforeClass
    public static void runOnceBeforeClass() {
    	CoapConfig.register();
		UdpConfig.register();
    	
        server = new CoAPLDPTestServer(BASE_URI);		
		server.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    @AfterClass
    public static void runOnceAfterClass() {
    	server.shutdown();
	}

	@Test
	public void getLDPRDFSource() throws Exception {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.TEXT_TURTLE);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
	
	@Test
	public void getLDPRDFSourceAsLDJSON() throws Exception {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.APPLICATION_LD_JSON);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
	
	@Test
	public void getLDPRDFSourceAsGZIP() throws Exception {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.APPLICATION_GZIP);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
	
	@Test
	public void getLDPRDFSourceAsBZIP2() throws Exception {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.APPLICATION_BZIP2);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
	
	@Test
	public void getLDPRDFSourceAsBSON() throws Exception {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.APPLICATION_BSON);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
	
	@Test
	public void getLDPRDFSourceAsUBJSON() throws Exception {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.APPLICATION_UBJSON);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
	
	@Test
	public void getLDPRDFSourceAsMsgPack() throws Exception {
		CoapClient client = new CoapClient(BASE_URI + "/alice");
		CoapResponse resp = client.get(MediaTypeRegistry.APPLICATION_MSGPACK);
		assertEquals(CoAP.ResponseCode.CONTENT, resp.getCode());
	}
}
