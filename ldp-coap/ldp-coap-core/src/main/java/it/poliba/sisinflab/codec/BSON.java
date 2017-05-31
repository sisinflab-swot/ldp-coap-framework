package it.poliba.sisinflab.codec;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;

public class BSON implements BasicCodec {		

	public byte[] encode(String msg) throws Exception {
		msg = msg.substring(1);
		msg = msg.substring(0, msg.length());
		HashMap<String,Object> result = new ObjectMapper().readValue(msg, HashMap.class);
		ObjectMapper mapper = new ObjectMapper(new BsonFactory());
	    return mapper.writeValueAsBytes(result);
	}

	public String decode(byte[] msg) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(msg);
		JsonParser parser = new BsonFactory().createJsonParser(bais);
		
		ObjectMapper bsoner = new ObjectMapper(new BsonFactory());
		// from is bson from an array [Object, Integer, Boolean]
		JsonNode node = bsoner.readTree(parser);
		
		return node.toString();
	}
	
	public String encodeAsString(String msg) throws Exception {
		return Arrays.toString(encode(msg));
	}

}
