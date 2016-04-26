package it.poliba.sisinflab.coap.ldp.raspberry;

import java.io.IOException;
import java.util.Date;

import org.openrdf.model.vocabulary.DCTERMS;

import com.pi4j.system.SystemInfo;

import it.poliba.sisinflab.coap.ldp.resources.LDPDataHandler;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

public class CPUTemperatureHandler extends LDPDataHandler {

	@Override
	protected void handleData() {
		double temp;
		try {
			temp = SystemInfo.getCpuTemperature();
			mng.updateRDFLiteralStatement(mng.getBaseURI() + resource, SSN_XG.hasValue.toString(), temp);
	    	mng.updateRDFLiteralStatement(mng.getBaseURI() + resource, DCTERMS.CREATED.toString(), new Date());
		} catch (NumberFormatException | IOException | InterruptedException e) {
			e.printStackTrace();
		}    	
	} 

}
