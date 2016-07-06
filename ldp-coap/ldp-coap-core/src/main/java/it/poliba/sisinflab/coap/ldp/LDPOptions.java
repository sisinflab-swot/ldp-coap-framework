package it.poliba.sisinflab.coap.ldp;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Includes objects and methods useful to manage all features required by LDP OPTIONS
 * <p> 
 * @see <a href="https://www.w3.org/TR/ldp/#h-ldpr-http_options">#LDP OPTIONS</a>
 *
 */

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
	
	/**
	 * Uses to indicate if a LDP method is allowed or not.
	 *
	 * @param  code 	the LDP method
	 * @param  value	true if the method is allowed	
	 * 
	 * @see LDP.Code
	 */
	public void setAllowedMethod(LDP.Code code, boolean value){
		allow.put(code, value);
	}
	
	/**
	 * Returns all LDP methods with the relative boolean value indicating if allowed or not 
	 *
	 * @return an HashMap with the elements of this set
	 * 
	 * @see LDP.Code
	 */
	public HashMap<LDP.Code, Boolean> getAllowedMethods(){
		return allow;
	}
	
	/**
	 * Verifies if a LDP method is allowed or not.
	 *
	 * @param  code 	the LDP method
	 * @return a boolean value indicating if allowed or not 
	 * 
	 * @see LDP.Code
	 */
	public boolean isAllowed(LDP.Code code){
		return allow.get(code);
	}
	
	/**
	 * Adds a resource Media Type accepted in a POST request
	 *
	 * @param  type 	the integer value associated to the CoAP resource Media Type, as defined in the MediaTypeRegistry class
	 * 
	 * @see MediaTypeRegistry
	 */
	public void addAcceptPostType(int type){
		acceptPost.add(type);
	}
	
	/**
	 * Returns all CoAP resource Media Types accepted in POST requests
	 *
	 * @return a list with the elements of this set
	 * 
	 * @see MediaTypeRegistry
	 */
	public ArrayList<Integer> getAcceptedPostTypes(){
		return acceptPost;
	}
	
	/**
	 * Verifies if a CoAP resource Media Type is accepted or not in a POST request
	 *
	 * @param  type 	the integer value associated to the CoAP resource Media Type
	 * @return a boolean value indicating if accepted or not 
	 * 
	 * @see MediaTypeRegistry
	 */
	public boolean isAcceptedPost(int type){
		return acceptPost.contains(type);
	}
	
	/**
	 * Adds a resource Media Type accepted in a PATCH request
	 *
	 * @param  type 	the integer value associated to the CoAP resource Media Type, as defined in the MediaTypeRegistry class
	 * 
	 * @see MediaTypeRegistry
	 */
	public void addAcceptPatchType(int type){
		acceptPatch.add(type);
	}
	
	/**
	 * Returns all CoAP resource Media Types accepted in PATCH requests
	 *
	 * @return a list with the elements of this set
	 * 
	 * @see MediaTypeRegistry
	 */
	public ArrayList<Integer> getAcceptedPatchTypes(){
		return acceptPatch;
	}
	
	/**
	 * Verifies if a CoAP resource Media Type is accepted or not in a PATCH request
	 *
	 * @param  type 	the integer value associated to the CoAP resource Media Type
	 * @return a boolean value indicating if accepted or not 
	 * 
	 * @see MediaTypeRegistry
	 */
	public boolean isAcceptedPatch(int type){
		return acceptPatch.contains(type);
	}
	
	/**
	 * Returns allowed operations, Accept-Post and Accept-Patch Media Types as a string in JSON format
	 *
	 * @return a string representing the JSONObject mapping for the OPTIONS features
	 * 
	 */
	public String toJSONString() throws JSONException {				
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
