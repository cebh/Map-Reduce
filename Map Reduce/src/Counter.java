import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;
/**
* This class implements the CounterInterface class and keeps track of guids
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
*/ 
public class Counter extends java.rmi.server.UnicastRemoteObject implements CounterInterface, Serializable {

	/**Represents the counter that keeps track of guids*/
	long counter =0;
	Registry registry;
	
	/**Set of guids*/
	Set<Long> set;
	
	public Counter() throws RemoteException
	{
		set = new HashSet<Long>();
		//registry = LocateRegistry.createRegistry(52000);
        //registry.rebind("Chord", this);
	}
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