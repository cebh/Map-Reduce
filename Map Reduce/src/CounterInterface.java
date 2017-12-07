import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;
/**
* This class is an interface that modifies the counter to make sure that the task is completed
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
*/
public interface CounterInterface extends Remote
{
	/**
	 * Adds a guid to the set
	 * @param key - guid
	 */
	public void add(long key) throws RemoteException;
	
	/**
	 * Checks if there are no more guids to be mapped
	 * @return true if there no more guids, otherwise false
	 */
	public Boolean hasCompleted() throws RemoteException;
	
	/**
	 * Increments counter and removes guid from the set
	 * @param key - guid to be removed
	 * @param n - value to increment by
	 */
	public void increment(long key, int n) throws RemoteException;
	
	/**
	 * Decrements counter
	 */
	public void decrement() throws RemoteException;
}
