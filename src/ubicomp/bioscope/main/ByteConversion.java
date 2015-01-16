package ubicomp.bioscope.main;

public class ByteConversion 
{
	public static float toFloat(byte data0, byte data1)
	{
		byte [] bytes = new byte [] {(byte) 0x00, (byte) 0x00, (byte) data0, (byte) data1};
		int asInt = (int)((bytes[0] & 0xFF) 
		           	| ((bytes[1] & 0xFF) << 8) 
		            | ((bytes[2] & 0xFF) << 16) 
		            | ((bytes[3] & 0xFF) << 24));
		float asFloat = Float.intBitsToFloat(asInt);
		return asFloat;
	}
	public static int toInt(byte data0, byte data1)
	{
		short val=(short)( ((data1&0xFF)<<8) | (data0&0xFF) );
		int asInt = (int) val;
		return asInt;
	}
}
