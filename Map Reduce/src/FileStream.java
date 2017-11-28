import java.io.*;
import java.nio.*;

/**
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen 
* This class contains methods to read files in the DFS
*/
public class FileStream extends InputStream implements  Serializable {
  
    /** Represents the cirrent positions in the file*/
    private int currentPosition;

     /** Represents the data from file*/
    private byte[] byteBuffer;

    /** Represents the file size*/
    private int size;

    /** 
    * Constructor that reads the data from file into byte array
    * @param pathName – path to the file
    */
    public  FileStream(String pathName) throws FileNotFoundException, IOException    
    {
      File file = new File(pathName);
      size = (int)file.length();
      byteBuffer = new byte[size];
      
      FileInputStream fileInputStream = new FileInputStream(pathName);
      int i = 0;
      while (fileInputStream.available()> 0)
      {
    	  byteBuffer[i++] = (byte)fileInputStream.read();
      }
      fileInputStream.close();	
      currentPosition = 0;	  
    }

    public  FileStream(byte[] data) throws FileNotFoundException, IOException    
    {
      //File file = new File(pathName);
      size = data.length;
      byteBuffer = data;
      
      /*FileInputStream fileInputStream = new FileInputStream(pathName);
      int i = 0;
      while (fileInputStream.available()> 0)
      {
        byteBuffer[i++] = (byte)fileInputStream.read();
      }
      fileInputStream.close();  */
      currentPosition = 0;    
    }
    
    /** 
    * Constructor that sets current position to = 0
    */
    public  FileStream() throws FileNotFoundException    
    {
      currentPosition = 0;	  
    }
    
    /**
    * Reads from file
    * @return content in byte buffer
    */
    public int read() throws IOException
    {
	 	if (currentPosition < size)
	 	  return (int)byteBuffer[currentPosition++];
	 	return 0;
    }
    
    /**
    * Checks if current position is still within array
    * @return size - currentPosition
    */
    public int available() throws IOException
    {
    	return size - currentPosition;
    }
}