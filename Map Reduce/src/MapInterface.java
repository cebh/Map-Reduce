import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
* This class represents an interface for the map phase
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
*/
public interface MapInterface  {
	/**
	 * Maps the guid and content to TreeMap
	 * @param key - guid
	 * @param value - page content
	 */
	public void map(int key, String value) throws IOException;
}

