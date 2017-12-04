import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;
/**
* This class implements the CounterInterface class and keeps track of guids
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
*/ 
public class Counter implements CounterInterface {

	/**Represents the counter that keeps track of guids*/
	long counter =0;
	
	/**Set of guids*/
	Set<Long> set;
	
	/**
	 * Adds a guid to the set
	 * @param key - guid
	 */
	public void add(long key)
	{
		set.add(key);
	}
	
	/**
	 * Checks if there are no more guids to be mapped
	 * @return true if there no more guids, otherwise false
	 */
	public Boolean hasCompleted()
	{
		if (counter == 0 && set.isEmpty())
			return true;
		return false;
	}
	
	/**
	 * Increments counter and removes guid from the set
	 * @param key - guid to be removed
	 * @param n - value to increment by
	 */
	public void increment(long key, int n) throws RemoteException
	{
		set.remove(key);
		counter+= n;
	}
	
	/**
	 * Decrements counter
	 */
	public void decrement() throws RemoteException
	{
		counter--;
	}
}