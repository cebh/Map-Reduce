import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

// counter class keeps track of guids
public class Counter implements CounterInterface {
	long counter =0;
	Set<Integer> set;
	public void add(int key)
	{
		set.add(key);
	}
	public Boolean hasCompleted()
	{
		if (counter == 0 && set.isEmpty())
			return true;
		return false;
	}
	public void increment(int key, int n) throws RemoteException
	{
		set.remove(key);
		counter+= n;
	}
	public void decrement() throws RemoteException
	{
		counter--;
	}
}