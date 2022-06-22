/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/
package it.poliba.sisinflab.coap.ldp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.SystemConfig;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.proxy2.ClientSingleEndpoint;
import org.eclipse.californium.proxy2.config.Proxy2Config;
import org.eclipse.californium.proxy2.http.HttpClientFactory;
import org.eclipse.californium.proxy2.http.server.ProxyHttpServer;
import org.eclipse.californium.proxy2.resources.ForwardProxyMessageDeliverer;
import org.eclipse.californium.proxy2.resources.ProxyCoapClientResource;
import org.eclipse.californium.proxy2.resources.ProxyCoapResource;
import org.eclipse.californium.proxy2.resources.ProxyHttpClientResource;

/**
 * Http2CoAP: Insert in browser:
 *     URI: http://localhost:8080/proxy/coap://localhost:PORT/target
 * 
 * CoAP2CoAP: Insert in Copper:
 *     URI: coap://localhost:PORT/coap2coap
 *     Proxy: coap://localhost:PORT/targetA
 *
 * CoAP2Http: Insert in Copper:
 *     URI: coap://localhost:PORT/coap2http
 *     Proxy: http://lantersoft.ch/robots.txt
 */
public class LDPCrossProxy {
	
	private static final String COAP2HTTP = "coap2http";
	private static final String COAP2COAP = "coap2coap";
	private static final int DEFAULT_BLOCK_SIZE = 1024;
	
	private ProxyHttpServer httpProxyServer = null;
	private CoapServer coapProxyServer = null;
	
	static {
		CoapConfig.register();
		UdpConfig.register();
		TcpConfig.register();
		Proxy2Config.register();
	}
	
	private Configuration getProxyConfig() {
		Configuration config = Configuration.getStandard();
		config.set(CoapConfig.MAX_ACTIVE_PEERS, 20000);
		config.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, 20000);
		config.set(CoapConfig.MAX_MESSAGE_SIZE, DEFAULT_BLOCK_SIZE);
		config.set(CoapConfig.PREFERRED_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
		config.set(CoapConfig.DEDUPLICATOR, CoapConfig.DEDUPLICATOR_PEERS_MARK_AND_SWEEP);
		config.set(CoapConfig.MAX_PEER_INACTIVITY_PERIOD, 24, TimeUnit.HOURS);
		config.set(Proxy2Config.HTTP_PORT, 8080);
		config.set(Proxy2Config.HTTP_CONNECTION_IDLE_TIMEOUT, 10, TimeUnit.SECONDS);
		config.set(Proxy2Config.HTTP_CONNECT_TIMEOUT, 15, TimeUnit.SECONDS);
		config.set(Proxy2Config.HTTPS_HANDSHAKE_TIMEOUT, 30, TimeUnit.SECONDS);
		config.set(UdpConfig.UDP_RECEIVE_BUFFER_SIZE, 8192);
		config.set(UdpConfig.UDP_SEND_BUFFER_SIZE, 8192);
		config.set(SystemConfig.HEALTH_STATUS_INTERVAL, 60, TimeUnit.SECONDS);
		return config;
	}
	
	private void startBasicForwardingProxy2(Configuration config) throws IOException {
		// initialize http clients
		HttpClientFactory.setNetworkConfig(config);
		// initialize coap-server
		int port = config.get(CoapConfig.COAP_PORT);
		coapProxyServer = new CoapServer(config, port);
		// initialize proxy resource
		ProxyCoapResource coap2http = new ProxyHttpClientResource(COAP2HTTP, false, false, null);
		// initialize proxy message deliverer
		ForwardProxyMessageDeliverer proxyMessageDeliverer = new ForwardProxyMessageDeliverer(coap2http);
		// set proxy message deliverer
		coapProxyServer.setMessageDeliverer(proxyMessageDeliverer);
		coapProxyServer.start();
		System.out.println("** CoAP Proxy started at: coap://localhost:" + port);
	}
	
	private void startBasicHttpForwardingProxy2(Configuration config) throws IOException {

		// initialize coap outgoing endpoint
		CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
		builder.setConfiguration(config);
		CoapEndpoint endpoint = builder.build();		
		ClientSingleEndpoint outgoingEndpoint = new ClientSingleEndpoint(endpoint);
		endpoint.start();

		ProxyCoapResource coap2coap = new ProxyCoapClientResource(COAP2COAP, false, false, null, outgoingEndpoint);
		ForwardProxyMessageDeliverer proxyMessageDeliverer = new ForwardProxyMessageDeliverer(coap2coap);

		httpProxyServer = ProxyHttpServer.buider()
				.setConfiguration(config)
				.setExecutor(endpoint)
				.setProxyCoapDeliverer(proxyMessageDeliverer)
				.build();

		httpProxyServer.start();

		System.out.println("** HTTP Proxy started at: http://" + httpProxyServer.getInterface().getHostName() + ":" + httpProxyServer.getInterface().getPort());
	}
	
	public LDPCrossProxy() throws IOException {
		Configuration proxyConfig = getProxyConfig();
		proxyConfig.set(Proxy2Config.HTTP_CONNECTION_IDLE_TIMEOUT, 60, TimeUnit.SECONDS);
		proxyConfig.set(Proxy2Config.HTTP_CONNECT_TIMEOUT, 120, TimeUnit.SECONDS);
		proxyConfig.set(Proxy2Config.HTTPS_HANDSHAKE_TIMEOUT, 60, TimeUnit.SECONDS);
		
		// startBasicForwardingProxy2(proxyConfig);
		startBasicHttpForwardingProxy2(proxyConfig);
	}
	
	public String getProxyAddress() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getProxyPort() {
		return Configuration.getStandard().get(CoapConfig.COAP_PORT);
	}

}
