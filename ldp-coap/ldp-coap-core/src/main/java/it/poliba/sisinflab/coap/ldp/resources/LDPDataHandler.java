package it.poliba.sisinflab.coap.ldp.resources;

/**
 * Retrieve data from a sensing device and update the LDP RDF repository 
 *
 */

public abstract class LDPDataHandler {
	
	protected int SLEEP_TIME = 2500; 
	protected Thread t;
	
	protected String resource; 
	protected CoAPLDPResourceManager mng;
	
	/**
	 * Creates a new data handler.
	 *
	 */
	public LDPDataHandler() {	
		t = new Thread() {
		    public void run() {
		    	while (true){
			    	
		    		handleData();
		    		
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						//e.printStackTrace();
						Thread.currentThread().interrupt();
					}
		    	}
		    }  
		};		
	}
	
	protected abstract void handleData();
	
	/**
	 * Initializes the data handler.
	 *
	 * @param  	resource 	the name of the reference resource in the RDF repository
	 * @param	mng			the reference resource manager
	 * 
	 * @see CoAPLDPResourceManager
	 */
	public void init(String resource, CoAPLDPResourceManager mng){
		this.resource = resource;
		this.mng = mng;
	}
	
	/**
	 * Starts to retrieve data and update the repository
	 *
	 */
	public void start(){
		t.start();
	}
	
	/**
	 * Stops to retrieve data and update the repository
	 *
	 */
	public void stop(){
		t.interrupt();
	}	
	
	/**
	 * Set the sampling period
	 *
	 * @param ms	the samping period in ms
	 */
	public void setSamplingPeriod(int ms){
		this.SLEEP_TIME = ms;
	}

}
