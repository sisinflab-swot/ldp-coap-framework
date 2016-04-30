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
 *    Martin Lanter - architect and re-implementation
 *    Francesco Corazza - HTTP cross-proxy
 *    Paul LeMarquand - fix content type returned from getHttpEntity(), cleanup
 ******************************************************************************/
package org.eclipse.californium.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Message;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionNumberRegistry;
import org.eclipse.californium.core.coap.OptionNumberRegistry.optionFormats;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class providing the translations (mappings) from the HTTP message
 * representations to the CoAP message representations and vice versa.
 */
public final class HttpTranslator {

	private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final String KEY_COAP_CODE = "coap.response.code.";
	private static final String KEY_COAP_OPTION = "coap.message.option.";
	private static final String KEY_COAP_MEDIA = "coap.message.media.";
	private static final String KEY_HTTP_CODE = "http.response.code.";
	private static final String KEY_HTTP_METHOD = "http.request.method.";
	private static final String KEY_HTTP_HEADER = "http.message.header.";
	private static final String KEY_HTTP_CONTENT_TYPE = "http.message.content-type.";
	
	private static String slug = "";
	private static String rt = "";
	private static String ldpServerAddress = "";
	private static String proxyIP = "";
	private static String ldp_incl = "";
	private static String ldp_omit = "";
	

	/**
	 * Property file containing the mappings between coap messages and http
	 * messages.
	 */
	public static final Properties HTTP_TRANSLATION_PROPERTIES = new MappingProperties("Proxy.properties");

	// Error constants
	public static final int STATUS_TIMEOUT = HttpStatus.SC_GATEWAY_TIMEOUT;
	public static final int STATUS_NOT_FOUND = HttpStatus.SC_BAD_GATEWAY;
	public static final int STATUS_TRANSLATION_ERROR = HttpStatus.SC_BAD_GATEWAY;
	public static final int STATUS_URI_MALFORMED = HttpStatus.SC_BAD_REQUEST;
	public static final int STATUS_WRONG_METHOD = HttpStatus.SC_NOT_IMPLEMENTED;

	protected static final Logger LOGGER = Logger.getLogger(HttpTranslator.class.getName());

	/**
	 * Gets the coap media type associated to the http entity. Firstly, it looks
	 * for a valid mapping in the property file. If this step fails, then it
	 * tries to explicitly map/parse the declared mime/type by the http entity.
	 * If even this step fails, it sets application/octet-stream as
	 * content-type.
	 * 
	 * @param httpMessage
	 * 
	 * 
	 * @return the coap media code associated to the http message entity. * @see
	 *         HttpHeader, ContentType, MediaTypeRegistry
	 */
	public static int getCoapMediaType(HttpMessage httpMessage) {
		if (httpMessage == null) {
			throw new IllegalArgumentException("httpMessage == null");
		}

		// get the entity
		HttpEntity httpEntity = null;
		if (httpMessage instanceof HttpResponse) {
			httpEntity = ((HttpResponse) httpMessage).getEntity();
		} else if (httpMessage instanceof HttpEntityEnclosingRequest) {
			httpEntity = ((HttpEntityEnclosingRequest) httpMessage).getEntity();
		}

		// check that the entity is actually present in the http message
		if (httpEntity == null) {
			throw new IllegalArgumentException("The http message does not contain any httpEntity.");
		}

		// set the content-type with a default value
		int coapContentType = MediaTypeRegistry.UNDEFINED;

		// get the content-type from the entity
		ContentType contentType = ContentType.get(httpEntity);
		if (contentType == null) {
			// if the content-type is not set, search in the headers
			Header contentTypeHeader = httpMessage.getFirstHeader("content-type");
			if (contentTypeHeader != null) {
				String contentTypeString = contentTypeHeader.getValue();
				contentType = ContentType.parse(contentTypeString);
			}
		}

		// check if there is an associated content-type with the current http
		// message
		if (contentType != null) {
			// get the value of the content-type
			String httpContentTypeString = contentType.getMimeType();
			// delete the last part (if any)
			httpContentTypeString = httpContentTypeString.split(";")[0];

			// retrieve the mapping from the property file
			String coapContentTypeString = HTTP_TRANSLATION_PROPERTIES.getProperty(KEY_HTTP_CONTENT_TYPE + httpContentTypeString);

			if (coapContentTypeString != null) {
				coapContentType = Integer.parseInt(coapContentTypeString);
			} else {
				// try to parse the media type if the property file has given to
				// mapping
				coapContentType = MediaTypeRegistry.parse(httpContentTypeString);
			}
		}

		// if not recognized, the content-type should be
		// application/octet-stream (draft-castellani-core-http-mapping 6.2)
		if (coapContentType == MediaTypeRegistry.UNDEFINED) {
			coapContentType = MediaTypeRegistry.APPLICATION_OCTET_STREAM;
		}

		return coapContentType;
	}

	/**
	 * Gets the coap options starting from an array of http headers. The
	 * content-type is not handled by this method. The method iterates over an
	 * array of headers and for each of them tries to find a mapping in the
	 * properties file, if the mapping does not exists it skips the header
	 * ignoring it. The method handles separately certain headers which are
	 * translated to options (such as accept or cache-control) whose content
	 * should be semantically checked or requires ad-hoc translation. Otherwise,
	 * the headers content is translated with the appropriate format required by
	 * the mapped option.
	 * 
	 * @param headers
	 * 
	 */
	public static List<Option> getCoapOptions(Header[] headers) {
		if (headers == null) {
			throw new IllegalArgumentException("httpMessage == null");
		}

		List<Option> optionList = new LinkedList<Option>();

		// iterate over the headers
		for (Header header : headers) {
			try {
				String headerName = header.getName().toLowerCase();
				
				// FIXME: CoAP does no longer support multiple accept-options.
				// If an HTTP request contains multiple accepts, this method
				// fails. Therefore, we currently skip accepts at the moment.
				if (headerName.startsWith("accept") && !(headerName.equals("accept")))
						continue;
				
				if(headerName.equals("slug")){
					slug = header.getValue().trim();
				}
				
				if(headerName.equals("prefer")){
					String value = header.getValue().trim();
					if (value.contains("include")){
						int start = value.indexOf("include=");
						String content = value.substring(start+9, value.length());
						int start_val = content.indexOf("#");
						String prefer = content.substring(start_val+1);
						if(!prefer.contains(" ")){
							ldp_incl="ldp:"+prefer.substring(0,prefer.length()-1);	
						}
						else ldp_incl="\"ldp:"+prefer.replace("http://www.w3.org/ns/ldp#", "ldp:");
	
					}else if(value.contains("omit")){
						int start = value.indexOf("omit=");
						String content = value.substring(start+6, value.length());
						int start_val = content.indexOf("#");
						String prefer = content.substring(start_val+1);
						if(!prefer.contains(" ")){
							ldp_omit="ldp:"+prefer.substring(0,prefer.length()-1);	
						}
						else ldp_omit="\"ldp:"+prefer.replace("http://www.w3.org/ns/ldp#", "ldp:");
					}
				}
				
				if(headerName.equals("link")){
					//slug = header.getValue().trim();
					int index = header.getValue().indexOf(";");
					String name = header.getValue().substring(0, index-1);
					index = name.indexOf("#");
					rt = name.substring(index+1);
				}
	
				// get the mapping from the property file
				String optionCodeString = HTTP_TRANSLATION_PROPERTIES.getProperty(KEY_HTTP_HEADER + headerName);
	
				// ignore the header if not found in the properties file
				if (optionCodeString == null || optionCodeString.isEmpty()) {
					continue;
				}
	
				// get the option number
				int optionNumber = OptionNumberRegistry.RESERVED_0;
				try {
					optionNumber = Integer.parseInt(optionCodeString.trim());
				} catch (Exception e) {
					LOGGER.warning("Problems in the parsing: " + e.getMessage());
					// ignore the option if not recognized
					continue;
				}
	
				// ignore the content-type because it will be handled within the
				// payload
				if (optionNumber == OptionNumberRegistry.CONTENT_FORMAT) {
					continue;
				}
	
				// get the value of the current header
				String headerValue = header.getValue().trim();
	
				// if the option is accept, it needs to translate the
				// values
				if (optionNumber == OptionNumberRegistry.ACCEPT) {
					// remove the part where the client express the weight of each
					// choice
					headerValue = headerValue.trim().split(";")[0].trim();
	
					// iterate for each content-type indicated
					for (String headerFragment : headerValue.split(",")) {
						// translate the content-type
						Integer[] coapContentTypes = { MediaTypeRegistry.UNDEFINED };
						if(headerFragment.equals("*/*")){
							coapContentTypes[0] = MediaTypeRegistry.parse("text/turtle");
						} else if (headerFragment.contains("*")) {
							coapContentTypes = MediaTypeRegistry.parseWildcard(headerFragment);
						} else {
							coapContentTypes[0] = MediaTypeRegistry.parse(headerFragment);
						}
	
						// if is present a conversion for the content-type, then add
						// a new option
						for (int coapContentType : coapContentTypes) {
							if (coapContentType != MediaTypeRegistry.UNDEFINED) {
								// create the option
								Option option = new Option(optionNumber, coapContentType);
								optionList.add(option);
							}
						}
					}
				} else if (optionNumber == OptionNumberRegistry.MAX_AGE) {
					int maxAge = 0;
					if (!headerValue.contains("no-cache")) {
						headerValue = headerValue.split(",")[0];
						if (headerValue != null) {
							int index = headerValue.indexOf('=');
							try {
								maxAge = Integer.parseInt(headerValue.substring(index + 1).trim());
							} catch (NumberFormatException e) {
								LOGGER.warning("Cannot convert cache control in max-age option");
								continue;
							}
						}
					}
					// create the option
					Option option = new Option(optionNumber, maxAge);
					// option.setValue(headerValue.getBytes(Charset.forName("ISO-8859-1")));
					optionList.add(option);
				} else {
					// create the option
					Option option = new Option(optionNumber);
					switch (OptionNumberRegistry.getFormatByNr(optionNumber)) {
					case INTEGER:
						option.setIntegerValue(Integer.parseInt(headerValue));
						break;
					case OPAQUE:
						option.setValue(headerValue.getBytes(ISO_8859_1));
						break;
					case STRING:
					default:
						option.setStringValue(headerValue);
						break;
					}
					// option.setValue(headerValue.getBytes(Charset.forName("ISO-8859-1")));
					optionList.add(option);
				}
			} catch (RuntimeException e) {
				// Martin: I have added this try-catch block. The problem is
				// that HTTP support multiple Accepts while CoAP does not. A
				// headder line might look like this:
				// Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
				// This cannot be parsed into a single CoAP Option and yields a
				// NumberFormatException
				LOGGER.warning("Could not parse header line "+header);
			}
		} // while (headerIterator.hasNext())

		return optionList;
	}

	/**
	 * Method to map the http entity of a http message in a coherent payload for
	 * the coap message. The method simply gets the bytes from the entity and,
	 * if needed changes the charset of the obtained bytes to UTF-8.
	 * 
	 * @param httpEntity
	 *            the http entity
	 * 
	 * @return byte[]
	 * @throws TranslationException
	 *             the translation exception
	 */
	public static byte[] getCoapPayload(HttpEntity httpEntity) throws TranslationException {
		if (httpEntity == null) {
			throw new IllegalArgumentException("httpEntity == null");
		}

		byte[] payload = null;
		try {
			// get the bytes from the entity
			payload = EntityUtils.toByteArray(httpEntity);
			if (payload != null && payload.length > 0 && looksLikeUTF8(payload)) {

				//modifica il payload per sostituire i riferimenti a http://proxyIP:8080/proxy/
			
				String body = "";
				try {
				body = new String(payload, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				}

				body = body.replace("http://" + proxyIP+ ":8080/proxy/", "coap://");
				
				payload = body.getBytes();
				
				// the only supported charset in CoAP is UTF-8
				Charset coapCharset = UTF_8;

				// get the charset for the http entity
				ContentType httpContentType = ContentType.getOrDefault(httpEntity);
				Charset httpCharset = httpContentType.getCharset();
				
				// check if the charset is the one allowed by coap
				if (httpCharset != null && !httpCharset.equals(coapCharset)) {
					// translate the payload to the utf-8 charset
					payload = changeCharset(payload, httpCharset, coapCharset);
				}
			}
			else {
				int i = 0;
			}
			
		} catch (IOException e) {
			LOGGER.warning("Cannot get the content of the http entity: " + e.getMessage());
			throw new TranslationException("Cannot get the content of the http entity", e);
		} finally {
			try {
				// ensure all content has been consumed, so that the
				// underlying connection could be re-used
				EntityUtils.consume(httpEntity);
			} catch (IOException e) {

			}
		}
		
		return payload;
	}
	static boolean looksLikeUTF8(byte[] utf8) 
	{
	  boolean response = false;
	  Pattern p = Pattern.compile("\\A(\n" +
	    "  [\\x09\\x0A\\x0D\\x20-\\x7E]             # ASCII\\n" +
	    "| [\\xC2-\\xDF][\\x80-\\xBF]               # non-overlong 2-byte\n" +
	    "|  \\xE0[\\xA0-\\xBF][\\x80-\\xBF]         # excluding overlongs\n" +
	    "| [\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}  # straight 3-byte\n" +
	    "|  \\xED[\\x80-\\x9F][\\x80-\\xBF]         # excluding surrogates\n" +
	    "|  \\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}      # planes 1-3\n" +
	    "| [\\xF1-\\xF3][\\x80-\\xBF]{3}            # planes 4-15\n" +
	    "|  \\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}      # plane 16\n" +
	    ")*\\z", Pattern.COMMENTS);

	  String phonyString;
	try {
		phonyString = new String(utf8, "ISO-8859-1");
		response = p.matcher(phonyString).matches();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		response = false;
	}
	  return response;
	}
	/**
	 * Gets the coap request. Creates the CoAP request from the HTTP method and
	 * mapping it through the properties file. The uri is translated using
	 * regular expressions, the uri format expected is either the embedded
	 * mapping (http://proxyname.domain:80/proxy/coapserver:5683/resource
	 * converted in coap://coapserver:5683/resource) or the standard uri to
	 * indicate a local request not to be forwarded. The method uses a decoder
	 * to translate the application/x-www-form-urlencoded format of the uri. The
	 * CoAP options are set translating the headers. If the HTTP message has an
	 * enclosing entity, it is converted to create the payload of the CoAP
	 * message; finally the content-type is set accordingly to the header and to
	 * the entity type.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @param proxyResource
	 *            the proxy resource, if present in the uri, indicates the need
	 *            of forwarding for the current request
	 * @param proxyingEnabled
	 *            TODO
	 * 
	 * 
	 * @return the coap request * @throws TranslationException the translation
	 *         exception
	 */
	public static Request getCoapRequest(HttpRequest httpRequest, String proxyResource, boolean proxyingEnabled) throws TranslationException {
		if (httpRequest == null) {
			throw new IllegalArgumentException("httpRequest == null");
		}
		if (proxyResource == null) {
			throw new IllegalArgumentException("proxyResource == null");
		}

		// get the http method
		String httpMethod = httpRequest.getRequestLine().getMethod().toLowerCase(); 

		// get the coap method
		String coapMethodString = HTTP_TRANSLATION_PROPERTIES.getProperty(KEY_HTTP_METHOD + httpMethod);
		if (coapMethodString == null || coapMethodString.contains("error")) {
			throw new InvalidMethodException(httpMethod + " method not mapped");
		}

		int coapMethod = 0;
		try {
			coapMethod = Integer.parseInt(coapMethodString.trim());
		} catch (NumberFormatException e) {
			LOGGER.warning("Cannot convert the http method in coap method: " + e);
			throw new TranslationException("Cannot convert the http method in coap method", e);
		}

		// create the request -- since HTTP is reliable use CON
		Request coapRequest = new Request(Code.valueOf(coapMethod), Type.CON);
		
		// translate the http headers in coap options
		List<Option> coapOptions = getCoapOptions(httpRequest.getAllHeaders());
		for (Option option:coapOptions)
			coapRequest.getOptions().addOption(option);			

		// get the uri
		String uriString = httpRequest.getRequestLine().getUri();
		// remove the initial "/"
		uriString = uriString.substring(1);
		
		//gestisco l'header Slug per il metodo POST
		if(httpMethod.equals("post")){
			if (rt != null && rt != "") {
				if (slug != null && slug != "") {
					uriString = uriString + "?title=" + slug + "&rt=ldp:" + rt;
					slug = "";
				} else {
					uriString = uriString + "?rt=ldp:" + rt;
				}
			} else {
				if (slug != null && slug != "") {
					uriString = uriString + "?title=" + slug + "&rt=ldp:Resource";
					slug = "";
				} else {
					uriString = uriString + "?rt=ldp:Resource";
				}
			}
		}else if(httpMethod.equals("patch")){
			uriString = uriString + "?ldp=patch";
		}else if(httpMethod.equals("get")){
			if(ldp_incl != null && ldp_incl != ""){
				if(!uriString.contains("?")){
					uriString = uriString + "?ldp-incl="+ldp_incl;
				}else{
					uriString = uriString + "&ldp-incl="+ldp_incl;
				}
				ldp_incl = "";
			}
			
			if(ldp_omit != null && ldp_omit != ""){
				if(!uriString.contains("?")){
					uriString = uriString + "?ldp-omit="+ldp_omit;
				}else{
					uriString = uriString + "&ldp-omit="+ldp_omit;
				}
				ldp_omit = "";
			}
		}

		// decode the uri to translate the application/x-www-form-urlencoded
		// format
		try {
			uriString = URLDecoder.decode(uriString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.warning("Failed to decode the uri: " + e.getMessage());
			throw new TranslationException("Failed decoding the uri: " + e.getMessage());
		} catch (Throwable e) {
			LOGGER.warning("Malformed uri: " + e.getMessage());
			throw new InvalidFieldException("Malformed uri: " + e.getMessage());
		}

		// if the uri contains the proxy resource name, the request should be
		// forwarded and it is needed to get the real requested coap server's
		// uri
		// e.g.:
		// /proxy/vslab-dhcp-17.inf.ethz.ch:5684/helloWorld
		// proxy resource: /proxy
		// coap server: vslab-dhcp-17.inf.ethz.ch:5684
		// coap resource: helloWorld
		if (uriString.matches(".?" + proxyResource + ".*")) {

			// find the first occurrence of the proxy resource
			int index = uriString.indexOf(proxyResource);
			// delete the slash
			index = uriString.indexOf('/', index);
			uriString = uriString.substring(index + 1);

			if (proxyingEnabled) {
				// if the uri hasn't the indication of the scheme, add it
				if (!uriString.matches("^coaps?://.*")) {
					uriString = "coap://" + uriString;
				}
				
				// the uri will be set as a proxy-uri option
				coapRequest.getOptions().setProxyUri(uriString.replace(" ", "%20").replace("\"", "%22"));
			} else {
				coapRequest.setURI(uriString);
			}

			// set the proxy as the sender to receive the response correctly
			try {
				// TODO check with multihomed hosts
				InetAddress localHostAddress = InetAddress.getLocalHost();
				//get proxy IP
				proxyIP = localHostAddress.getHostAddress();
				coapRequest.setDestination(localHostAddress);
				// TODO: setDestinationPort???
			} catch (UnknownHostException e) {
				LOGGER.warning("Cannot get the localhost address: " + e.getMessage());
				throw new TranslationException("Cannot get the localhost address: " + e.getMessage());
			}
		} else {
			// if the uri does not contains the proxy resource, it means the
			// request is local to the proxy and it shouldn't be forwarded

			// set the uri string as uri-path option
			coapRequest.getOptions().setUriPath(uriString);
		}

		// set the payload if the http entity is present
		if (httpRequest instanceof HttpEntityEnclosingRequest) {
			HttpEntity httpEntity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();

			// translate the http entity in coap payload
			byte[] payload = getCoapPayload(httpEntity);
			coapRequest.setPayload(payload);

			// set the content-type
			int coapContentType = getCoapMediaType(httpRequest);
			coapRequest.getOptions().setContentFormat(coapContentType);
		}
		//retrieve LDP server address
		String provvisorio = uriString.substring(7);
		int end = provvisorio.indexOf("/");
		ldpServerAddress = provvisorio.substring(0, end);
		return coapRequest;
	}
	
	public static Request createCoapRequestDiscovery(String proxyUri) throws TranslationException{
		// create the request -- since HTTP is reliable use CON
	    Request coapRequest = new Request(Code.valueOf(1), Type.CON);
	    
	    String uri = "";
	    if(proxyUri.contains("?")){
	    	int index = proxyUri.indexOf("?");
	    	uri = proxyUri.substring(0, index);
	    }else {
	    	uri = proxyUri;
	    }
	    
	    String uriNoSchema = uri.substring(7);
	    int index = uriNoSchema.indexOf('/', 0);
	    String host = uriNoSchema.substring(0, index);
	    String resource = uriNoSchema.substring(index + 1);
	    
	    StringTokenizer tokenizer = new StringTokenizer(resource, "/");
		String resources = ""; 
		
		while(tokenizer.hasMoreElements()){
			resources = (String)tokenizer.nextElement();
		}
	    
	    coapRequest.getOptions().setProxyUri("coap://"+host+"/.well-known/core?title="+resources);
	    
		// set the proxy as the sender to receive the response correctly
		try {
			// TODO check with multihomed hosts
			InetAddress localHostAddress = InetAddress.getLocalHost();
			coapRequest.setDestination(localHostAddress);
			// TODO: setDestinationPort???
		} catch (UnknownHostException e) {
			LOGGER.warning("Cannot get the localhost address: "
					+ e.getMessage());
			throw new TranslationException("Cannot get the localhost address: "
					+ e.getMessage());
		}

	    return coapRequest;
	}
	
	public static Request createCoapRequestOptions(String proxyUri) throws TranslationException{
		// create the request -- since HTTP is reliable use CON
	    Request coapRequest = new Request(Code.valueOf(1), Type.CON);
	 
	    if(proxyUri.contains("?")){
	    	int index = proxyUri.indexOf("?");
	    	String uri = proxyUri.substring(0, index);
	    	coapRequest.getOptions().setProxyUri(uri+"?ldp=options");
	    }else {
	    	coapRequest.getOptions().setProxyUri(proxyUri+"?ldp=options");
	    }
	    
		// set the proxy as the sender to receive the response correctly
		try {
			// TODO check with multihomed hosts
			InetAddress localHostAddress = InetAddress.getLocalHost();
			coapRequest.setDestination(localHostAddress);
			// TODO: setDestinationPort???
		} catch (UnknownHostException e) {
			LOGGER.warning("Cannot get the localhost address: "
					+ e.getMessage());
			throw new TranslationException("Cannot get the localhost address: "
					+ e.getMessage());
		}

		return coapRequest;
	}

	/**
	 * Gets the CoAP response from an incoming HTTP response. No null value is
	 * returned. The response is created from a the mapping of the HTTP response
	 * code retrieved from the properties file. If the code is 204, which has
	 * multiple meaning, the mapping is handled looking on the request method
	 * that has originated the response. The options are set thorugh the HTTP
	 * headers and the option max-age, if not indicated, is set to the default
	 * value (60 seconds). if the response has an enclosing entity, it is mapped
	 * to a CoAP payload and the content-type of the CoAP message is set
	 * properly.
	 * 
	 * @param httpResponse
	 *            the http response
	 * @param coapRequest
	 * 
	 * 
	 * @return the coap response * @throws TranslationException the translation
	 *         exception
	 */
	public static Response getCoapResponse(HttpResponse httpResponse, Request coapRequest) throws TranslationException {
		if (httpResponse == null) {
			throw new IllegalArgumentException("httpResponse == null");
		}
		if (coapRequest == null) {
			throw new IllegalArgumentException("coapRequest == null");
		}

		// get/set the response code
		int httpCode = httpResponse.getStatusLine().getStatusCode();
		ResponseCode coapCode;
		Code coapMethod = coapRequest.getCode();

		// the code 204-"no content" should be managed
		// separately because it can be mapped to different coap codes
		// depending on the request that has originated the response
		if (httpCode == HttpStatus.SC_NO_CONTENT) {
			if (coapMethod == Code.DELETE) {
				coapCode = ResponseCode.DELETED;
			} else {
				coapCode = ResponseCode.CHANGED;
			}
		} else {
			// get the translation from the property file
			String coapCodeString = HTTP_TRANSLATION_PROPERTIES.getProperty(KEY_HTTP_CODE + httpCode);

			if (coapCodeString == null || coapCodeString.isEmpty()) {
				LOGGER.warning("coapCodeString == null");
				throw new TranslationException("coapCodeString == null");
			}

			try {
				coapCode = ResponseCode.valueOf(Integer.parseInt(coapCodeString.trim()));
			} catch (NumberFormatException e) {
				LOGGER.warning("Cannot convert the status code in number: " + e.getMessage());
				throw new TranslationException("Cannot convert the status code in number", e);
			}
		}

		// create the coap reaponse
		Response coapResponse = new Response(coapCode);

		// translate the http headers in coap options
		List<Option> coapOptions = getCoapOptions(httpResponse.getAllHeaders());

		for (Option option:coapOptions)
			coapResponse.getOptions().addOption(option);

		// the response should indicate a max-age value (RFC 7252, Section 10.1.1)
		if (!coapResponse.getOptions().hasMaxAge()) {
			// The Max-Age Option for responses to POST, PUT or DELETE requests
			// should always be set to 0 (draft-castellani-core-http-mapping).
			if (coapMethod == Code.GET) {
				coapResponse.getOptions().setMaxAge(OptionNumberRegistry.Defaults.MAX_AGE);
			} else {
				coapResponse.getOptions().setMaxAge(0);
			}
		}

		// get the entity
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			// translate the http entity in coap payload
			byte[] payload = getCoapPayload(httpEntity);

			
			if (payload != null && payload.length > 0) {
				coapResponse.setPayload(payload);

				// set the content-type
				int coapContentType = getCoapMediaType(httpResponse);
				coapResponse.getOptions().setContentFormat(coapContentType);
			}
		}

		return coapResponse;
	}

	/**
	 * Generates an HTTP entity starting from a CoAP request. If the coap
	 * message has no payload, it returns a null http entity. It takes the
	 * payload from the CoAP message and encapsulates it in an entity. If the
	 * content-type is recognized, and a mapping is present in the properties
	 * file, it is translated to the correspondent in HTTP, otherwise it is set
	 * to application/octet-stream. If the content-type has a charset, namely it
	 * is printable, the payload is encapsulated in a StringEntity, if not it a
	 * ByteArrayEntity is used.
	 * 
	 * 
	 * @param coapMessage
	 *            the coap message
	 * 
	 * 
	 * @return null if the request has no payload * @throws TranslationException
	 *         the translation exception
	 */
	public static HttpEntity getHttpEntity(Message coapMessage) throws TranslationException {
		if (coapMessage == null) {
			throw new IllegalArgumentException("coapMessage == null");
		}

		// the result
		HttpEntity httpEntity = null;

		// check if coap request has a payload
		byte[] payload = coapMessage.getPayload();
		if (payload != null && payload.length != 0) {
			
			
			ContentType contentType = null;

			// if the content type is not set, translate with octect-stream
			if (! coapMessage.getOptions().hasContentFormat()) {
				contentType = ContentType.APPLICATION_OCTET_STREAM;
			} else {
				int coapContentType = coapMessage.getOptions().getContentFormat();
				// search for the media type inside the property file
				String coapContentTypeString = HTTP_TRANSLATION_PROPERTIES.getProperty(KEY_COAP_MEDIA + coapContentType);

				// if the content-type has not been found in the property file,
				// try to get its string value (expressed in mime type)
				if (coapContentTypeString == null || coapContentTypeString.isEmpty()) {
					coapContentTypeString = MediaTypeRegistry.toString(coapContentType);

					// if the coap content-type is printable, it is needed to
					// set the default charset (i.e., UTF-8)
					if (MediaTypeRegistry.isPrintable(coapContentType)) {
						coapContentTypeString += "; charset=UTF-8";
					}
				}

				// parse the content type
				try {
					contentType = ContentType.parse(coapContentTypeString);
				} catch (UnsupportedCharsetException e) {
					LOGGER.finer("Cannot convert string to ContentType: " + e.getMessage());
					contentType = ContentType.APPLICATION_OCTET_STREAM;
				}
			}

			// get the charset
			Charset charset = contentType.getCharset();

			// if there is a charset, means that the content is not binary
			if (charset != null) {
				String body = "";
				try {
					body = new String(payload, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				body = body.replace("coap://", "http://" + proxyIP + ":8080/proxy/");
				payload = body.getBytes();
				// according to the class ContentType the default content-type
				// with UTF-8 charset is application/json. If the content-type
				// parsed is different and is not iso encoded, a translation is
				// needed
				Charset isoCharset = ISO_8859_1;
				/*if (!charset.equals(isoCharset) && !contentType.getMimeType().equals(ContentType.APPLICATION_JSON.getMimeType())) {
					byte[] newPayload = changeCharset(payload, charset, isoCharset);

					// since ISO-8859-1 is a subset of UTF-8, it is needed to
					// check if the mapping could be accomplished, only if the
					// operation is succesful the payload and the charset should
					// be changed
					if (newPayload != null) {
						payload = newPayload;
						// if the charset is changed, also the entire
						// content-type must change
						contentType = ContentType.create(contentType.getMimeType(), isoCharset);
					}
				}*/
				
				// create the content
				String payloadString = new String(payload, contentType.getCharset());

				// create the entity
				httpEntity = new StringEntity(payloadString, ContentType.create(contentType.getMimeType(), contentType.getCharset()));
			} else {
				// create the entity
				httpEntity = new ByteArrayEntity(payload);
			}

			// set the content-type
			((AbstractHttpEntity) httpEntity).setContentType(contentType.getMimeType());
		}

		return httpEntity;
	}

	/**
	 * Gets the http headers from a list of CoAP options. The method iterates
	 * over the list looking for a translation of each option in the properties
	 * file, this process ignores the proxy-uri and the content-type because
	 * they are managed differently. If a mapping is present, the content of the
	 * option is mapped to a string accordingly to its original format and set
	 * as the content of the header.
	 * 
	 * 
	 * @param optionList
	 *            the coap message
	 * 
	 * @return Header[]
	 */
	public static Header[] getHttpHeaders(List<Option> optionList, String host) {
		if (optionList == null) {
			throw new IllegalArgumentException("coapMessage == null");
		}

		List<Header> headers = new LinkedList<Header>();
		
		String location_path = "";
		String location_query = "";

		// iterate over each option
		for (Option option : optionList) { 
			
			if(option.toString().startsWith("Location-Path:")){
				if(location_path != null && location_path != ""){
					location_path = location_path + "/" + option.getStringValue();
				}else{
					location_path = "http://" + proxyIP + ":8080/" + host + "/" + ldpServerAddress + "/" + option.getStringValue();
				} 
			}else if(option.toString().startsWith("Location-Query:")){
				String res = option.getStringValue();
				if(res.contains("ldp-incl") || res.contains("ldp-omit")){
					Header header = new BasicHeader("Preference-Applied","return=representation");
					headers.add(header);
				}else {
					location_query = "<http://www.w3.org/ns/ldp#";
					int index = res.indexOf(":");
					String value = res.substring(index+1);
					location_query = location_query + value + ">; rel='type'";
					Header header = new BasicHeader("Link",location_query);
					headers.add(header);
				}
			}else {
			// skip content-type because it should be translated while handling
			// the payload; skip proxy-uri because it has to be translated in a
			// different way
			int optionNumber = option.getNumber();
				if (optionNumber != OptionNumberRegistry.CONTENT_FORMAT
						&& optionNumber != OptionNumberRegistry.PROXY_URI) {
					// get the mapping from the property file
					String headerName = HTTP_TRANSLATION_PROPERTIES
							.getProperty(KEY_COAP_OPTION + optionNumber);

					// set the header
					if (headerName != null && !headerName.isEmpty()) {
						// format the value
						String stringOptionValue = null;
						if (OptionNumberRegistry.getFormatByNr(optionNumber) == optionFormats.STRING) {
							stringOptionValue = option.getStringValue();
						} else if (OptionNumberRegistry
								.getFormatByNr(optionNumber) == optionFormats.INTEGER) {
							stringOptionValue = Integer.toString(option
									.getIntegerValue());
						} else if (OptionNumberRegistry
								.getFormatByNr(optionNumber) == optionFormats.OPAQUE) {
							stringOptionValue = new String(option.getValue());
						} else {
							// if the option is not formattable, skip it
							continue;
						}

						// custom handling for max-age
						// format: cache-control: max-age=60
						if (optionNumber == OptionNumberRegistry.MAX_AGE) {
							stringOptionValue = "max-age=" + stringOptionValue;
						}

						Header header = new BasicHeader(headerName,
								stringOptionValue);
						headers.add(header);
					}
				}
			}
		}
		
		if(location_path != null && location_path != ""){
			Header header = new BasicHeader("Location",location_path);
			headers.add(header);
		}

		return headers.toArray(new Header[0]);
	}

	/**
	 * Gets the http request starting from a CoAP request. The method creates
	 * the HTTP request through its request line. The request line is built with
	 * the uri coming from the string representing the CoAP method and the uri
	 * obtained from the proxy-uri option. If a payload is provided, the HTTP
	 * request encloses an HTTP entity and consequently the content-type is set.
	 * Finally, the CoAP options are mapped to the HTTP headers.
	 * 
	 * @param coapRequest
	 *            the coap request
	 * 
	 * 
	 * 
	 * @return the http request * @throws TranslationException the translation
	 *         exception * @throws URISyntaxException the uRI syntax exception
	 */
	public static HttpRequest getHttpRequest(Request coapRequest) throws TranslationException {
		if (coapRequest == null) {
			throw new IllegalArgumentException("coapRequest == null");
		}

		HttpRequest httpRequest = null;

		String coapMethod = null;
		switch (coapRequest.getCode()) {
		case GET: coapMethod = "GET"; break;
		case POST: coapMethod = "POST"; break;
		case PUT: coapMethod = "PUT"; break;
		case DELETE: coapMethod = "DELETE"; break;
		}

		// get the proxy-uri
		URI proxyUri;
		try {
			/*
			 * The new draft (14) only allows one proxy-uri option. Thus, this
			 * code segment has changed.
			 */
			String proxyUriString = URLDecoder.decode(
					coapRequest.getOptions().getProxyUri(), "UTF-8");
			proxyUri = new URI(proxyUriString);
		} catch (UnsupportedEncodingException e) {
			LOGGER.warning("UTF-8 do not support this encoding: " + e);
			throw new TranslationException("UTF-8 do not support this encoding", e);
		} catch (URISyntaxException e) {
			LOGGER.warning("Cannot translate the server uri" + e);
			throw new InvalidFieldException("Cannot get the proxy-uri from the coap message", e);
		}

		// create the requestLine
		RequestLine requestLine = new BasicRequestLine(coapMethod, proxyUri.toString(), HttpVersion.HTTP_1_1);

		// get the http entity
		HttpEntity httpEntity = getHttpEntity(coapRequest);

		// create the http request
		if (httpEntity == null) {
			httpRequest = new BasicHttpRequest(requestLine);
		} else {
			httpRequest = new BasicHttpEntityEnclosingRequest(requestLine);
			((HttpEntityEnclosingRequest) httpRequest).setEntity(httpEntity);

			// get the content-type from the entity and set the header
			ContentType contentType = ContentType.get(httpEntity);
			httpRequest.setHeader("content-type", contentType.toString());
		}

		// set the headers
		Header[] headers = getHttpHeaders(coapRequest.getOptions().asSortedList(), "");
		for (Header header : headers) {
			httpRequest.addHeader(header);
		}

		return httpRequest;
	}
	
	/**
	 * Sets the parameters of the incoming http response from a CoAP response.
	 * The status code is mapped through the properties file and is set through
	 * the StatusLine. The options are translated to the corresponding headers
	 * and the max-age (in the header cache-control) is set to the default value
	 * (60 seconds) if not already present. If the request method was not HEAD
	 * and the coap response has a payload, the entity and the content-type are
	 * set in the http response.
	 * 
	 * @param coapResponse
	 *            the coap response
	 * @param httpResponse
	 * 
	 * 
	 * 
	 * @param httpRequest
	 *            HttpRequest
	 * @throws TranslationException
	 *             the translation exception
	 */
	public static void getHttpResponse(HttpRequest httpRequest, Response coapResponse, HttpResponse httpResponse) throws TranslationException {
		if (httpRequest == null) {
			throw new IllegalArgumentException("httpRequest == null");
		}
		if (coapResponse == null) {
			throw new IllegalArgumentException("coapResponse == null");
		}
		if (httpResponse == null) {
			throw new IllegalArgumentException("httpResponse == null");
		}

		// get/set the response code
		ResponseCode coapCode = coapResponse.getCode();
		String httpCodeString = HTTP_TRANSLATION_PROPERTIES.getProperty(KEY_COAP_CODE + coapCode.value);

		if (httpCodeString == null || httpCodeString.isEmpty()) {
			LOGGER.warning("httpCodeString == null");
			throw new TranslationException("httpCodeString == null");
		}

		int httpCode = 0;
		try {
			httpCode = Integer.parseInt(httpCodeString.trim());
		} catch (NumberFormatException e) {
			LOGGER.warning("Cannot convert the coap code in http status code" + e);
			throw new TranslationException("Cannot convert the coap code in http status code", e);
		}

		// create the http response and set the status line
		String reason = EnglishReasonPhraseCatalog.INSTANCE.getReason(httpCode, Locale.ENGLISH);
		StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, httpCode, reason);
		httpResponse.setStatusLine(statusLine);

		String uriString = httpRequest.getRequestLine().getUri();
		int index_query = uriString.indexOf("//");
		String query = uriString.substring(index_query+2);
		int index_host = query.indexOf("/");
		String host = query.substring(0, index_host);
		
		// set the headers
		Header[] headers = getHttpHeaders(coapResponse.getOptions().asSortedList(), host);
		httpResponse.setHeaders(headers);

		// set max-age if not already set
		if (!httpResponse.containsHeader("cache-control")) {
			httpResponse.setHeader("cache-control", "max-age=" + Long.toString(OptionNumberRegistry.Defaults.MAX_AGE));
		}

		// get the http entity if the request was not HEAD
		if (!httpRequest.getRequestLine().getMethod().equalsIgnoreCase("head")) {
			if ((httpRequest.getRequestLine().getMethod().equalsIgnoreCase("put")) && (coapCode.value == 131)) {
				String linkPut = getLinkPut(coapResponse);
				Header link = new BasicHeader("Link", linkPut);
				httpResponse.addHeader(link);
			}
			// if the content-type is not set in the coap response and if the
			// response contains an error, then the content-type should set to
			// text-plain
			if (coapResponse.getOptions().getContentFormat() == MediaTypeRegistry.UNDEFINED
					&& (ResponseCode.isClientError(coapCode) || ResponseCode.isServerError(coapCode))) {
				LOGGER.info("Set contenttype to TEXT_PLAIN");
				coapResponse.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
			}

			HttpEntity httpEntity = getHttpEntity(coapResponse);
			if (httpEntity != null) {
				httpResponse.setEntity(httpEntity);

				// get the content-type from the entity and set the header
				ContentType contentType = ContentType.get(httpEntity);
				httpResponse.setHeader("content-type", contentType.toString());
			}
		} 
	}
	//recupero l'header Link dalla put che mi restituisce 403
	static String getLinkPut(Response coapResponse){
		String linkHeader = "";
		if (coapResponse == null) {
			throw new IllegalArgumentException("coapMessage == null");
		}

		// check if coap request has a payload
		byte[] payload = coapResponse.getPayload();
		if (payload != null && payload.length != 0) {

			//modifica il payload per sostituire i riferimenti a coap://

			String body = "";
			try {
				body = new String(payload, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(body.startsWith("Link")){
				linkHeader = body.substring(6);
			}

		}

		return linkHeader;

	}
	/**
	 * Change charset.
	 * 
	 * @param payload
	 *            the payload
	 * @param fromCharset
	 *            the from charset
	 * @param toCharset
	 *            the to charset
	 * 
	 * 
	 * @return the byte[] * @throws TranslationException the translation
	 *         exception
	 */
	private static byte[] changeCharset(byte[] payload, Charset fromCharset, Charset toCharset) throws TranslationException {
		try {
			// decode with the source charset
			CharsetDecoder decoder = fromCharset.newDecoder();
			CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap(payload));
			decoder.flush(charBuffer);

			// encode to the destination charset
			CharsetEncoder encoder = toCharset.newEncoder();
			ByteBuffer byteBuffer = encoder.encode(charBuffer);
			encoder.flush(byteBuffer);
			payload = byteBuffer.array();
		} catch (UnmappableCharacterException e) {
			// thrown when an input character (or byte) sequence is valid but
			// cannot be mapped to an output byte (or character) sequence.
			// If the character sequence starting at the input buffer's current
			// position cannot be mapped to an equivalent byte sequence and the
			// current unmappable-character
			LOGGER.finer("Charset translation: cannot mapped to an output char byte: " + e.getMessage());
			return null;
		} catch (CharacterCodingException e) {
			LOGGER.warning("Problem in the decoding/encoding charset: " + e.getMessage());
			throw new TranslationException("Problem in the decoding/encoding charset", e);
		}

		return payload;
	}

	/**
	 * The Constructor is private because the class is an helper class and
	 * cannot be instantiated.
	 */
	private HttpTranslator() {

	}

	public static void getHttpDiscovery(HttpRequest httpRequest,Response coapResponse, HttpResponse httpResponse) {
		// TODO Auto-generated method stub
		String response = coapResponse.getPayloadString();

		int start_index = response.indexOf('"', 0);
		String substring = response.substring(start_index+1);
		int end_index = substring.indexOf('"', 0);
		String rt = substring.substring(0, end_index);
		StringTokenizer tokenizer = new StringTokenizer(rt, " ");
		String resources = ""; 
		
		while(tokenizer.hasMoreElements()){
			String resource = "<"+(String)tokenizer.nextElement() + ">; rel=\"type\", ";
			resources = resources + resource;
		}
		
		resources = resources.substring(0, resources.length()-2);
		Header link = new BasicHeader("Link", resources);
		httpResponse.addHeader(link);		
		
	}

	public static void getHttpOptions(HttpRequest httpRequest,Response coapResponse, HttpResponse httpResponse) {
		// TODO Auto-generated method stub
		String response = coapResponse.getPayloadString();
		
		JSONParser parser=new JSONParser();
		try {
			JSONObject obj= (JSONObject) parser.parse(response);
			JSONArray allow = (JSONArray) obj.get("Allow"); 
			JSONArray accept_post = (JSONArray) obj.get("Accept-Post");
			JSONArray accept_patch = (JSONArray) obj.get("Accept-Patch");
			if(allow != null){
				String allow_string = allow.toJSONString();
				allow_string = allow_string.replace("\"", "");
				allow_string = allow_string.replace("[", "");
				allow_string = allow_string.replace("]", "");
				Header allow_header = new BasicHeader("Allow", allow_string);
				httpResponse.addHeader(allow_header);
			}
			
			if(accept_post != null){
				String accept_post_string = accept_post.toJSONString();
				accept_post_string = accept_post_string.replace("\"", "");
				accept_post_string = accept_post_string.replace("\\", "");
				accept_post_string = accept_post_string.replace("[", "");
				accept_post_string = accept_post_string.replace("]", "");
				Header accept_post_header = new BasicHeader("Accept-Post", accept_post_string);
				httpResponse.addHeader(accept_post_header);
			}
			
			if(accept_patch != null){
				String accept_patch_string = accept_patch.toJSONString();
				accept_patch_string = accept_patch_string.replace("\"", "");
				accept_patch_string = accept_patch_string.replace("\\", "");
				accept_patch_string = accept_patch_string.replace("[", "");
				accept_patch_string = accept_patch_string.replace("]", "");
				Header accept_patch_header = new BasicHeader("Accept-Patch", accept_patch_string);
				httpResponse.addHeader(accept_patch_header);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
