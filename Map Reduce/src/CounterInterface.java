import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

// counter to make sure task is completed
public interface CounterInterface extends Remote {
	public void add(int key) throws RemoteException;
	public void increment(int key, int n) throws RemoteException;
	public void decrement() throws RemoteException;
}
