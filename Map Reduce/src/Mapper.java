import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;
/**
* This class implements the MapInterface class and ReduceInterface class for map reducing
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
*/
public class Mapper implements MapInterface, ReduceInterface  {
	/**
	 * Maps the guid and content to TreeMap
	 * @param key - guid
	 * @param value - page content
	 */
	public void map(int key, String value) throws IOException
	{
		//For each word in value
		//emit(md5(word), word +":"+1);
	}
	
	/**
	 * Removes repeated keys in the map
	 * @param key -  guid
	 * @param value - page content
	 */
	public void reduce(int key, String values[]) throws IOException
	{
		//word = values[0].split(":")[0];
		//emit(key, word +":"+ len(values));
	}
}
