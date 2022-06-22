package it.poliba.sisinflab.coap.ldp.raspberry;

import java.io.IOException;
import java.util.Date;

import org.eclipse.rdf4j.model.vocabulary.DCTERMS;

import com.pi4j.system.SystemInfo;

import it.poliba.sisinflab.coap.ldp.resources.LDPDataHandler;
import it.poliba.sisinflab.rdf.vocabulary.SSN_XG;

/**
 * Retrieves data about the CPU Temperature and update the LDP RDF repository 
 * <p>
 * @see <a href="http://pi4j.com/">Pi4j Library</a>
 *
 */

public class CPUTemperatureHandler extends LDPDataHandler {

	@Override
	protected void handleData() {
		double temp;
		try {
			temp = SystemInfo.getCpuTemperature();
			mng.updateRDFLiteralStatement(mng.getBaseURI() + resource, SSN_XG.hasValue.stringValue(), temp);
	    	mng.updateRDFLiteralStatement(mng.getBaseURI() + resource, DCTERMS.CREATED.stringValue(), new Date());
		} catch (NumberFormatException | IOException | InterruptedException e) {
			e.printStackTrace();
		}    	
	} 

}
