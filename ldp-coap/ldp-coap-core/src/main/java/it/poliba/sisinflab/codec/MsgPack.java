package it.poliba.sisinflab.codec;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MsgPack implements BasicCodec {

	public byte[] encode(String msg) throws Exception {
		msg = msg.substring(1);
		msg = msg.substring(0, msg.length());
		HashMap<String,Object> result = new ObjectMapper().readValue(msg, HashMap.class);
		ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
	    return mapper.writeValueAsBytes(result);
	}
	
	public String encodeAsString(String msg) throws Exception {
		return Arrays.toString(encode(msg));
	}

	public String decode(byte[] msg) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(msg);
		JsonParser parser = new MessagePackFactory().createJsonParser(bais);
		ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

		JsonNode node = mapper.readTree(parser);		
		return node.toString();
	}

}
