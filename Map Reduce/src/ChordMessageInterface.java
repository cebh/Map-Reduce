import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
* This class is an interface for Chord.
*/
public interface ChordMessageInterface extends Remote
{

   /**
    * Get predecessor node
    * @return predecessor node
    */
    public ChordMessageInterface getPredecessor()  throws RemoteException;
    
    /**
    * Locates a successor node's GUID in the ring
    * @param key – GUID of the predecessor
    * @return successor node's GUID in the ring
    */
    ChordMessageInterface locateSuccessor(long key) throws RemoteException;
    
    /**
    * Gets the closest preceding node
    * @param key – current node's GUID
    * @return closest preceding node
    */
    ChordMessageInterface closestPrecedingNode(long key) throws RemoteException;
    
    /**
    * Joins a peer to the ring
    * @param ip – peer's ip address
    * @param port – port number
    */
    public void joinRing(String Ip, int port)  throws RemoteException;
    
    /**
    * Sends a message and notifies a peer
    * @param j – node to be notified
    */
    public void notify(ChordMessageInterface j) throws RemoteException;
    
    /**
    * Checks if a node is still active in the connection
    * @return true
    */
    public boolean isAlive() throws RemoteException;
    
    /**
    * Gets the guid
    * @return guid of a node
    */
    public long getId() throws RemoteException;
        
    /**
    * Stores data in the processor responsible for storing the object
    * @param guidObject –file name
    * @param stream  – data to be stored
    */
    public void put(long guidObject, InputStream inputStream) throws IOException, RemoteException;
    
    /**
    * Retrieves the data associated with GUID from one of the nodes responsible for it
    * @param guidObject –file name
    * @return data in a file
    */
    public InputStream get(long guidObject) throws IOException, RemoteException;
    
    /**
    * Deletes  all  references  to  GUID  and  the  associated data
    * @param guidObject –file name
    */
    public void delete(long guidObject) throws IOException, RemoteException;

    /**
     * Map phase that sorts the keys by adding them to a TreeMap
     * @param key - guid
     * @param value - page content
     * @param counter - counter object
     */
    void emitMap(long key, String value, Counter counter) throws RemoteException;

    /**
     * Removes repeated keys from the map
     * @param key - guid
     * @param value - page content
     * @param counter - counter object
     */
    void emitReduce(long key, String value, Counter counter) throws RemoteException;

    /**
     * Reads the page and maps the content
     * @param key - guid
     * @param mapper - interface that maps the content
     * @param counter - counter object
     */
    void mapContext(long key, MapInterface mapper, Counter counter) throws RemoteException;

    /**
     * Removes the repeated content
     * @param key - guid
     * @param mapper - interface that maps the content
     * @param counter - counter object
     */
    void reduceContext(int source, ReduceInterface reducer, Counter counter) throws RemoteException;
    
    /**
     * Create a new file that stores the tree in the file output in  page guid
     * @param source - source id
     * @param counter - counter object
     */
    void completed(int source, Counter counter) throws RemoteException;
    
}
