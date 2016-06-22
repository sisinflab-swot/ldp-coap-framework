package it.poliba.sisinflab.coap.ldp.raspberry;

import java.io.IOException;
import java.util.Date;

import org.eclipse.rdf4j.model.vocabulary.DCTERMS;

import com.pi4j.system.SystemInfo;

import it.poliba.sisinflab.coap.ldp.resources.LDPDataHandler;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

public class FreeMemoryHandler extends LDPDataHandler {

	@Override
	protected void handleData() {
		double free;
		try {
			free = SystemInfo.getMemoryFree();
			mng.updateRDFLiteralStatement(mng.getBaseURI() + resource, SSN_XG.hasValue.toString(), free);
	    	mng.updateRDFLiteralStatement(mng.getBaseURI() + resource, DCTERMS.CREATED.toString(), new Date());
		} catch (NumberFormatException | IOException | InterruptedException e) {
			e.printStackTrace();
		}    	
	} 

}
