package it.poliba.sisinflab.codec;

public interface BasicCodec {
	
	public byte[] encode(String msg) throws Exception;
	public String encodeAsString(String msg) throws Exception;
	public String decode(byte[] msg) throws Exception;
	
}
