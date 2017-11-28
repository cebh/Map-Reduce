import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

// reduce phase
public interface ReduceInterface  {
	public void reduce(int key, String value[]) throws IOException;
}