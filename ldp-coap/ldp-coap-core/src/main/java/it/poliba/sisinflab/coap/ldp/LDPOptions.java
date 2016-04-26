package it.poliba.sisinflab.coap.ldp;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LDPOptions {
	
	HashMap<LDP.Code, Boolean> allow;
	ArrayList<Integer> acceptPost;
	ArrayList<Integer> acceptPatch;
	
	public LDPOptions(){
		
		/*** Init Allow ***/
		allow = new HashMap<LDP.Code, Boolean>();
		allow.put(LDP.Code.GET, false);
		allow.put(LDP.Code.POST, false);
		allow.put(LDP.Code.PUT, false);
		allow.put(LDP.Code.DELETE, false);
		allow.put(LDP.Code.PATCH, false);
		allow.put(LDP.Code.HEAD, false);
		allow.put(LDP.Code.OPTIONS, false);
		
		/*** Init Accept-Post ***/
		acceptPost = new ArrayList<Integer>();
		
		/*** Init Accept-Patch ***/
		acceptPatch = new ArrayList<Integer>();
	}
	
	public void setAllowedMethod(LDP.Code code, boolean value){
		allow.put(code, value);
	}
	
	public HashMap<LDP.Code, Boolean> getAllowedMethods(){
		return allow;
	}
	
	public boolean isAllowed(LDP.Code code){
		return allow.get(code);
	}
	
	public void addAcceptPostType(int type){
		acceptPost.add(type);
	}
	
	public ArrayList<Integer> getAcceptedPostTypes(){
		return acceptPost;
	}
	
	public boolean isAcceptedPost(int type){
		return acceptPost.contains(type);
	}
	
	public void addAcceptPatchType(int type){
		acceptPatch.add(type);
	}
	
	public ArrayList<Integer> getAcceptedPatchTypes(){
		return acceptPatch;
	}
	
	public boolean isAcceptedPatch(int type){
		return acceptPatch.contains(type);
	}
	
	public String toJSONString() throws JSONException {
		/*JsonObjectBuilder bld = Json.createObjectBuilder();
		
		JsonArrayBuilder meth = Json.createArrayBuilder();
		for(LDP.Code c : allow.keySet()){
			if(isAllowed(c))
				meth.add(c.toString());
		}		
		bld.add(LDP.HDR_ALLOW, meth);
		
		if(acceptPost.size() > 0){
			JsonArrayBuilder post = Json.createArrayBuilder();
			for(int t : acceptPost){
				post.add(MediaTypeRegistry.toString(t));
			}		
			bld.add(LDP.HDR_ACCEPT_POST, post);
		}
		
		if(acceptPatch.size() > 0){
			JsonArrayBuilder patch = Json.createArrayBuilder();
			for(int t : acceptPatch){
				patch.add(MediaTypeRegistry.toString(t));
			}		
			bld.add(LDP.HDR_ACCEPT_PATCH, patch);
		}
		
		Writer sw = new StringWriter();
		JsonWriter wrt = Json.createWriter(sw);
		wrt.writeObject(bld.build());
		wrt.close();
		
		return sw.toString();*/
		
		JSONObject bld = new JSONObject();
		
		JSONArray meth = new JSONArray();
		for(LDP.Code c : allow.keySet()){
			if(isAllowed(c))
				meth.put(c.toString());
		}		
		bld.put(LDP.HDR_ALLOW, meth);
		
		if(acceptPost.size() > 0){
			JSONArray post = new JSONArray();
			for(int t : acceptPost){
				post.put(MediaTypeRegistry.toString(t));
			}		
			bld.put(LDP.HDR_ACCEPT_POST, post);
		}
		
		if(acceptPatch.size() > 0){
			JSONArray patch = new JSONArray();
			for(int t : acceptPatch){
				patch.put(MediaTypeRegistry.toString(t));
			}		
			bld.put(LDP.HDR_ACCEPT_PATCH, patch);
		}
		
	    return bld.toString();
	}

}
