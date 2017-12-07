import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;
/**
* This class represents an interface for reduce map phase
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
*/
public interface ReduceInterface  {
	/**
	 * Removes repeated keys in the map
	 * @param key -  guid
	 * @param value - page content
	 */
	public void reduce(long key, String value[], Counter counter) throws IOException;
}