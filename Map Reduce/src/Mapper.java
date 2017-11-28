import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;

// mapreduce class
public class Mapper implements MapInterface, ReduceInterface  {
	public void map(int key, String value) throws IOException
	{
		//For each word in value
		//emit(md5(word), word +":"+1);
	}
	public void reduce(int key, String values[]) throws IOException
	{
		//word = values[0].split(":")[0];
		//emit(key, word +":"+ len(values));
	}
}
