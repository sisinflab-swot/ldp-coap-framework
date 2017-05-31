package it.poliba.sisinflab.codec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class BZIP2 implements BasicCodec {

	public byte[] encode(String msg) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		CompressorOutputStream zip = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2, out);		
		zip.write(msg.getBytes("UTF-8"));
		zip.close();
		return out.toByteArray();
	}

	public String decode(byte[] msg) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(msg);
		BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(bais);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bzIn, "UTF-8"));

		String out = "";
		String line;
        while ((line = bufferedReader.readLine()) != null) {
            out += line;
        }
        
        bufferedReader.close();
        bzIn.close();
        bais.close();
		
		return out;
	}
	
	public String encodeAsString(String msg) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		CompressorOutputStream zip = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2, out);		
		zip.write(msg.getBytes("UTF-8"));
		zip.close();
		return out.toString();
	}
	
}
