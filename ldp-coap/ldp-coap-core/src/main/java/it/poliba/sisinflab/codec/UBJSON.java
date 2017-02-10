package it.poliba.sisinflab.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import com.devsmart.ubjson.UBArray;
import com.devsmart.ubjson.UBObject;
import com.devsmart.ubjson.UBReader;
import com.devsmart.ubjson.UBValue;
import com.devsmart.ubjson.UBValueFactory;
import com.devsmart.ubjson.UBWriter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UBJSON implements BasicCodec {
	
	UBObject ub = UBValueFactory.createObject();
	
	ObjectMapper mapper = new ObjectMapper();
	JsonGenerator gen = null;
	
	public String encodeAsString(String msg) throws Exception {
		JsonFactory factory = new JsonFactory();
		JsonParser  parser  = factory.createParser(msg);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		UBWriter writer = new UBWriter(out);
		UBArray a = null;

		while(!parser.isClosed()){
		    JsonToken token = parser.nextToken();		    
		    if(JsonToken.START_ARRAY.equals(token)){
		    	a = parserArray(parser);
		    }
		}
		
		writer.writeArray(a);
		writer.close();
		
		return out.toString();
	}

	public byte[] encode(String msg) throws Exception {
		JsonFactory factory = new JsonFactory();
		JsonParser  parser  = factory.createParser(msg);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		UBWriter writer = new UBWriter(out);
		UBArray a = null;

		while(!parser.isClosed()){
		    JsonToken token = parser.nextToken();		    
		    if(JsonToken.START_ARRAY.equals(token)){
		    	a = parserArray(parser);
		    }
		}
		
		writer.writeArray(a);
		writer.close();
		
		return out.toByteArray();				
	}

	private UBArray parserArray(JsonParser parser) throws Exception {
		
		ArrayList<UBValue> value = new ArrayList<UBValue>();
		
		JsonToken token = null;
		do {
			token = parser.nextToken();
			
			if(JsonToken.START_ARRAY.equals(token)){
				UBValue v = parserArray(parser);
				value.add(v);
		    } else if(JsonToken.START_OBJECT.equals(token)){
		    	UBValue v = parserObject(parser);
		    	value.add(v);
		    } else if(JsonToken.VALUE_STRING.equals(token)){
		    	UBValue v = UBValueFactory.createString(parser.getValueAsString());
		    	value.add(v);
		    }			
		} while(!JsonToken.END_ARRAY.equals(token));	
		
		return UBValueFactory.createArray(value);
	}

	private UBValue parserElement(JsonParser parser) throws Exception {
		JsonToken token = parser.nextToken();
		if(JsonToken.START_ARRAY.equals(token)){
	    	return parserArray(parser);
	    } else if(JsonToken.VALUE_STRING.equals(token)){
	    	return UBValueFactory.createString(parser.getValueAsString());
	    } else
	    	return null;
		
	}

	private UBObject parserObject(JsonParser parser) throws Exception {	
		UBObject obj = UBValueFactory.createObject();
		JsonToken token = null;
		do {
			token = parser.nextToken();
			
			if(JsonToken.FIELD_NAME.equals(token)){
		    	String name = parser.getCurrentName();
		    	UBValue v = parserElement(parser);
		    	obj.put(name, v);
		    } 			
		} while(!JsonToken.END_OBJECT.equals(token));	
		
		return obj;
	}

	public String decode(byte[] msg) throws Exception {			
		ByteArrayInputStream bais = new ByteArrayInputStream(msg);
		UBReader ur = new UBReader(bais);
		UBValue v = ur.read();
		
		String json = "";
		if (v.isArray()){
			ArrayNode array = decodeArray(v.asArray());		
			json = array.toString();
		}
		
		ur.close();		
		return json;
	}
	
	private ArrayNode decodeArray(UBArray a){
		ArrayNode array = mapper.createArrayNode();		
		for(int i=0; i<a.size(); i++){
			UBValue v = a.get(i);
			if (v.isArray()) {
				ArrayNode an = decodeArray(v.asArray());
				array.add(an);
			} else if (v.isObject()) {
				ObjectNode on = decodeObject(v.asObject());
				array.add(on);
			} else if (v.isString()) {
				array.add(v.asString());
			}			
		}
		return array;
	}

	private ObjectNode decodeObject(UBObject obj) {
		ObjectNode node = mapper.createObjectNode();
		for (String key : obj.keySet()){
			UBValue v = obj.get(key);
			if(v.isString()){				
				node.put(key, v.asString());
			} else if (v.isObject()) {
				ObjectNode on = decodeObject(v.asObject());
				node.put(key, on);
			} else if (v.isArray()) {
				ArrayNode an = decodeArray(v.asArray());
				node.put(key, an);
			}
		}
		return node;
	}

}
