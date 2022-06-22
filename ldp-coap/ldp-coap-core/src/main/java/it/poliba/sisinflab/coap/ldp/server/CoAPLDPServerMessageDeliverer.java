package it.poliba.sisinflab.coap.ldp.server;

import java.util.List;
import java.util.concurrent.Executor;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.DelivererException;
import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.poliba.sisinflab.coap.ldp.LDP;

/**
 * Extends the basic Californium CoAP ServerMessageDeliverer to enable LDP-CoAP PUT-to-Create features
 *
 * @see org.eclipse.californium.core.server.ServerMessageDeliverer
 */

public class CoAPLDPServerMessageDeliverer extends ServerMessageDeliverer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CoAPLDPServerMessageDeliverer.class);

	public CoAPLDPServerMessageDeliverer(Resource root) {
		super(root);
	}
	
	@Override
	public void deliverRequest(final Exchange exchange) {		
		Request request = exchange.getRequest();
		List<String> path = request.getOptions().getUriPath();
		Resource resource;
		try {
			resource = findResource(path);
			
			if (resource != null) {
				checkForObserveOption(exchange, resource);
				
				// Get the executor and let it process the request
				/*Executor executor = resource.getExecutor();
				if (executor != null) {
					exchange.setCustomExecutor();
					executor.execute(new Runnable() {
						public void run() {
							resource.handleRequest(exchange);
						} });
				} else {
					resource.handleRequest(exchange);
				}*/
				
				this.handleRequest(exchange, resource);
				
			} else {
				String slug = path.get(path.size()-1);
				List<String> parentPath = path.subList(0, path.size()-1);
				Resource parent;
				try {
					parent = findResource(parentPath);
					
					if (exchange.getCurrentRequest().getCode().equals(CoAP.Code.PUT) && 
							parent != null && parent.getAttributes().getResourceTypes().contains(LDP.CLASS_CONTAINER)){
						LOGGER.info("Put-to-Create: " + slug + " on " + parentPath.toString());		
														
						exchange.getCurrentRequest().getOptions().getUriQuery().add("title="+slug); 
						this.handleRequest(exchange, parent);
						
					} else {			
						LOGGER.info("Did not find resource " + path.toString());
						exchange.sendResponse(new Response(ResponseCode.NOT_FOUND));		
					}
				} catch (DelivererException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (DelivererException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void handleRequest(final Exchange exchange, final Resource resource){
		// Get the executor and let it process the request
		Executor executor = resource.getExecutor();
		if (executor != null) {
			//exchange.setCustomExecutor();
			executor.execute(new Runnable() {
				public void run() {
					resource.handleRequest(exchange);
				} });
		} else {
			resource.handleRequest(exchange);
		}
	}	

}
