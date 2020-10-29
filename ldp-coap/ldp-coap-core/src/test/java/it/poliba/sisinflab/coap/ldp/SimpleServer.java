package it.poliba.sisinflab.coap.ldp;

import java.net.SocketException;
import it.poliba.sisinflab.coap.ldp.server.CoAPLDPServer;

public class SimpleServer extends CoAPLDPServer {

    public SimpleServer(String BASE_URI) {
        super(BASE_URI);
        // try {
        // 	init();
        // } catch (SocketException | CoAPLDPException e) {
        // 	e.printStackTrace();
        // 	System.out.println("Error during server initialization!");
        // 	System.exit(0);
        // }
    }


    public static void main(String args[]) throws SocketException, InterruptedException {
        String BASE_URI = "coap://127.0.0.1:5683";

        if (args.length == 0) {
            System.out.println("Missing URL\nUsage: java -jar LdpCoapServer.jar URL\nURL must use coap:// scheme; if port is omitted, 5683 is assumed.");
        } else if (args.length > 1) {
            System.out.println("Too many arguments\nUsage: java -jar LdpCoapServer.jar URL\nURL must use coap:// scheme; if port is omitted, 5683 is assumed.");
        } else
            BASE_URI = args[0];

        SimpleServer server = new SimpleServer(BASE_URI);
        server.start();

        while (true) {
            Thread.sleep(500);
        }

    }
}
