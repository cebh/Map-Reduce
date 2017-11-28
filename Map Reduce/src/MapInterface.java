import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

// map phase
public interface MapInterface  {
	public void map(int key, String value) throws IOException;
}

