package it.poliba.sisinflab.codec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP implements BasicCodec {

	public byte[] encode(String msg) throws Exception {
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(msg.getBytes("UTF-8"));
		gzip.close();
		return obj.toByteArray();
	}

	public String decode(byte[] msg) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(msg);
		GZIPInputStream gzip = new GZIPInputStream(bais);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzip, "UTF-8"));

		String out = "";
		String line;
        while ((line = bufferedReader.readLine()) != null) {
            out += line;
        }
		
        bufferedReader.close();
        gzip.close();
        bais.close();
        
		return out;
	}

	public String encodeAsString(String msg) throws Exception {
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(msg.getBytes("UTF-8"));
		gzip.close();
		return obj.toString();
	}

}
