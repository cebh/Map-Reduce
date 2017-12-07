import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.net.*;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
/**
* This class implements the MapInterface class and ReduceInterface class for map reducing
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
*/
public class Mapper implements MapInterface, ReduceInterface  {
	private Chord chord;
	
	public Mapper(Chord c)
	{
		chord = c;
	}
	private long md5(String objectName)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(objectName.getBytes());
            BigInteger bigInt = new BigInteger(1,m.digest());
            return Math.abs(bigInt.longValue());
        }
        catch(NoSuchAlgorithmException e)
        {
                e.printStackTrace();
                
        }
        return 0;
    }
	
	/**
	 * Maps the guid and content to TreeMap
	 * @param key - guid
	 * @param value - page content
	 */
	public void map(long key, String value, Counter counter) throws IOException
	{
		//For each word in value
		chord.emitMap(key, value, counter);
	}
	
	/**
	 * Removes repeated keys in the map
	 * @param key -  guid
	 * @param value - page content
	 */
	public void reduce(long key, String values[], Counter counter) throws IOException
	{
		String word = values[0].split(":")[0];
		chord.emitReduce(key, word + ":"+ values.length, counter);
	}
}
