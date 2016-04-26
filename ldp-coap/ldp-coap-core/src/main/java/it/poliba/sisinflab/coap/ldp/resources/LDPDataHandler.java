package it.poliba.sisinflab.coap.ldp.resources;

public abstract class LDPDataHandler {
	
	protected int SLEEP_TIME = 2500; 
	protected Thread t;
	
	protected String resource; 
	protected CoAPLDPResourceManager mng;
	
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
	
	public void init(String resource, CoAPLDPResourceManager mng){
		this.resource = resource;
		this.mng = mng;
	}
	
	public void start(){
		t.start();
	}
	
	public void stop(){
		t.interrupt();
	}	
	
	public void setSamplingPeriod(int ms){
		this.SLEEP_TIME = ms;
	}

}
