import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.io.*;
/**
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
* This class represents a Peer to Peer system
*/
public class Chord extends java.rmi.server.UnicastRemoteObject implements ChordMessageInterface
{
    public static final int M = 2;
    
    /** rmi registry for lookup the remote objects.*/
    Registry registry;    

    /** Represents the node's sucessor*/
    ChordMessageInterface successor;

    /** Represents the node's predecessor*/
    ChordMessageInterface predecessor;

    /** Represents the node's pointers*/
    ChordMessageInterface[] finger;

    /** Represents the node's next pointer*/
    int nextFinger;

    /** Represents the node's GUID*/
    long guid;   	
    
    /**
    * Checks if two nodes are close to eack other in a semi-closed interval
    * @param key – distance
    * @param key1 – first key
    * @param key2 – second key
    * @return true if they are close, otherwise false 
    */
    public Boolean isKeyInSemiCloseInterval(long key, long key1, long key2)
    {
       if (key1 < key2)
           return (key > key1 && key <= key2);
      else
          return (key > key1 || key <= key2);
    }


    /**
    * Checks if two nodes are close to eack other in an open interval
    * @param key – distance
    * @param key1 – first key
    * @param key2 – second key
    * @return true if they are close, otherwise false 
    */
    public Boolean isKeyInOpenInterval(long key, long key1, long key2)
    {
      if (key1 < key2)
          return (key > key1 && key < key2);
      else
          return (key > key1 || key < key2);
    }
    
    /**
    * Stores data in the processor responsible for storing the object
    * @param guidObject –filename 
    * @param stream  – data to be stored
    */
    public void put(long guidObject, InputStream stream) throws RemoteException 
    {
      try 
      {
	          String fileName = "./"+guid+"/repository/" + guidObject;
	          FileOutputStream output = new FileOutputStream(fileName);
	          while (stream.available() > 0)
	              output.write(stream.read());
	          output.close();
      }
      catch (IOException e) 
      {
          System.out.println(e);
      }
    }
    
    /**
    * Retrieves the data associated with GUID from one of the nodes responsible for it
    * @param guidObject – file name
    * @return data in a file
    */
    public InputStream get(long guidObject) throws RemoteException 
    {
        FileStream file = null;
        try 
        {
             file = new FileStream("./"+guid+"/repository/" + guidObject);
        } catch (IOException e)
        {
            throw(new RemoteException("File does not exists"));
        }
        return file;
    }
    
    /**
    * Deletes  all  references  to  GUID  and  the  associated data
    * @param guidObject – file name
    */
    public void delete(long guidObject) throws RemoteException 
    {
        File file = new File("./"+guid+"/repository/" + guidObject);
        file.delete();
    }
    
    /**
    * Gets the guid
    * @return guid of a node
    */
    public long getId() throws RemoteException 
    {
        return guid;
    }

    /**
    * Checks if a node is still active in the connection
    * @return true
    */
    public boolean isAlive() throws RemoteException 
    {
	    return true;
    }
    

    /**
    * Get predecessor node
    * @return predecessor node
    */
    public ChordMessageInterface getPredecessor() throws RemoteException 
    {
	    return predecessor;
    }
    
    /**
    * Locates a successor node's GUID in the ring
    * @param key – GUID of the predecessor
    * @return successor node's GUID in the ring
    */
    public ChordMessageInterface locateSuccessor(long key) throws RemoteException 
    {
	    if (key == guid)
            throw new IllegalArgumentException("Key must be distinct that  " + guid);
	    if (successor.getId() != guid)
	    {
	      if (isKeyInSemiCloseInterval(key, guid, successor.getId()))
	        return successor;
	      ChordMessageInterface j = closestPrecedingNode(key);
	      
          if (j == null)
	        return null;
	      return j.locateSuccessor(key);
        }
        return successor;
    }
    
    /**
    * Gets the closest preceding node
    * @param key – current node's GUID
    * @return closest preceding node
    */
    public ChordMessageInterface closestPrecedingNode(long key) throws RemoteException 
    {
        // todo
        if(key != guid) 
        {
            int i = M - 1;
            while (i >= 0) 
            {
                try
                {
       
                    if(isKeyInSemiCloseInterval(finger[i].getId(), guid, key)) 
                    {
                        if(finger[i].getId() != key)
                            return finger[i];
                        else 
                        {
                            return successor;
                        }
                    }
                }
                catch(Exception e)
                {
                    // Skip ;
                }
                i--;
            }
        }
        return successor;
    }
    

    /**
    * Joins a peer to the ring
    * @param ip – peer's ip address
    * @param port – port number
    */
    public void joinRing(String ip, int port)  throws RemoteException 
    {
        try
        {
            System.out.println("Get Registry to joining ring");
            Registry registry = LocateRegistry.getRegistry(ip, port);
            ChordMessageInterface chord = (ChordMessageInterface)(registry.lookup("Chord"));
            predecessor = null;
            successor = chord.locateSuccessor(this.getId());
            System.out.println("Joining ring");
        }
        catch(RemoteException | NotBoundException e)
        {
            successor = this;
        }   
    }
    
    /**
    * Finds the next successor node
    */
    public void findingNextSuccessor()
    {
        int i;
        successor = this;
        for (i = 0;  i< M; i++)
        {
            try
            {
                if (finger[i].isAlive())
                {
                    successor = finger[i];
                }
            }
            catch(RemoteException | NullPointerException e)
            {
                finger[i] = null;
            }
        }
    }
    
    /**
    * Balances the ring
    */
    public void stabilize() 
    {
      try {
          if (successor != null)
          {
              ChordMessageInterface x = successor.getPredecessor();
	   
              if (x != null && x.getId() != this.getId() && isKeyInOpenInterval(x.getId(), this.getId(), successor.getId()))
              {
                  successor = x;
              }
              if (successor.getId() != getId())
              {
                  successor.notify(this);
              }
          }
      } catch(RemoteException | NullPointerException e1) 
      {
          findingNextSuccessor();

      }
    }
    
    /**
    * Sends a message and notifies a peer
    * @param j – node to be notified
    */
    public void notify(ChordMessageInterface j) throws RemoteException 
    {
         if (predecessor == null || (predecessor != null
                    && isKeyInOpenInterval(j.getId(), predecessor.getId(), guid)))
             predecessor = j;
            try 
            {
                File folder = new File("./"+guid+"/repository/");
                File[] files = folder.listFiles();
                for (File file : files) 
                {
                    long guidObject = Long.valueOf(file.getName());
                    if(guidObject < predecessor.getId() && predecessor.getId() < guid) 
                    {
                        predecessor.put(guidObject, new FileStream(file.getPath()));
                        file.delete();
                    }
                }
                } catch (ArrayIndexOutOfBoundsException e) 
            {
                //happens sometimes when a new file is added during foreach loop
            } catch (IOException e) 
            {
            e.printStackTrace();
        }

    }
    
    /**
    * Updates finger position
    */
    public void fixFingers() 
    {
    
        long id= guid;
        try 
        {
            long nextId = this.getId() + 1<< (nextFinger+1);
            finger[nextFinger] = locateSuccessor(nextId);
	    
            if (finger[nextFinger].getId() == guid)
                finger[nextFinger] = null;
            else
                nextFinger = (nextFinger + 1) % M;
        }
        catch(RemoteException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    /**
    * Checks if predecessor is active
    */
    public void checkPredecessor() 
    { 	
      try 
      {
          if (predecessor != null && !predecessor.isAlive())
              predecessor = null;
      } 
      catch(RemoteException e) 
      {
          predecessor = null;
//           e.printStackTrace();
      }
    }
    
     /**
    * Constructor for a Chord object
    * @param port – port number
    * @param guid – node id
    */   
    public Chord(int port, long guid) throws RemoteException 
    {
        int j;
	    finger = new ChordMessageInterface[M];
        for (j=0;j<M; j++)
        {
	       finger[j] = null;
     	}
        this.guid = guid;
	
        predecessor = null;
        successor = this;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() 
        {
	    @Override
	    public void run() 
	    {
            stabilize();
            fixFingers();
            checkPredecessor();
            }
        }, 500, 500);
        try
        {
            // create the registry and bind the name and object.
            System.out.println(guid + " is starting RMI at port="+port);
            registry = LocateRegistry.createRegistry( port );
            registry.rebind("Chord", this);
        }
        catch(RemoteException e){
	       throw e;
        } 
    }

    /**
    *  Print the state of the systems.
    */
    void Print()
    {   
        int i;
        try {
            if (successor != null)
                System.out.println("successor "+ successor.getId());
            if (predecessor != null)
                System.out.println("predecessor "+ predecessor.getId());
            for (i=0; i<M; i++)
            {
                try {
                    if (finger != null)
                        System.out.println("Finger "+ i + " " + finger[i].getId());
                } catch(NullPointerException e)
                {
                    finger[i] = null;
                }
            }
        }
        catch(RemoteException e){
	       System.out.println("Cannot retrive id");
        }
    }

    public void emitMap(int key, String value, Counter counter) throws RemoteException
    {
        if(isKeyInSemiCloseInterval(key, predecessor.getId(), guid)) {
            // TODO store key and value in TreeMap<Long,List<String>>
            counter.decrement();
        } else if(isKeyInSemiCloseInterval(key, guid, successor.getId())) {
            successor.emitMap(key, value, counter);
        } else {
            closestPrecedingNode(key).emitMap(key,value,counter);
        }
    }

    public void emitReduce(int key, String value, Counter counter) throws RemoteException {
        if(isKeyInSemiCloseInterval(key, predecessor.getId(), guid)) {
            // TODO store key and value in TreeMap<Long,String>
            counter.decrement();
        } else if(isKeyInSemiCloseInterval(key, guid, successor.getId())) {
            successor.emitReduce(key, value, counter);
        } else {
            closestPrecedingNode(key).emitReduce(key,value,counter);
        }
    }

    public void mapContext(int page, MapInterface mapper, Counter counter) throws RemoteException {
        // open page
        // for each line, mapper.map(key, value, counter)
        
    }

    public void reduceContext(int source, ReduceInterface reducer, Counter counter) throws RemoteException {
        // open page
        // for each line, mapper.map(key, value, counter)
        
    }

    public void completed(int source, Counter counter) throws RemoteException {

    }
}
