import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.math.BigInteger;
import java.security.*;
import com.google.gson.*;  // imported a json package
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
* This class represents a distributed file system that uses the chord interface
*/
public class DFS
{
    /**Port that this poor is using*/
    int port;

    /**Chord object*/
    Chord  chord;
    

    /**
    * Hash function to generate unique GUID
    * @param objectName – name of the peer
    * @preturn guid of the peer
    */
    private long md5(String objectName)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(objectName.getBytes());
            BigInteger bigInt = new BigInteger(1,m.digest());
            return Math.abs(bigInt.longValue());
        }
        catch(NoSuchAlgorithmException e)
        {
                e.printStackTrace();
                
        }
        return 0;
    }
    
    
    /**
    * Constructor for the distributed file system
    * @param port – port number that the peer is using
    */
    public DFS(int port) throws Exception
    {
        
        this.port = port;
        long guid = md5("" + port);
        chord = new Chord(port, guid);
        Files.createDirectories(Paths.get(guid+"/repository"));
    }
    

    /**
    * Joins the peer to the ring
    * @param Ip – ip address
    * @param port – port number
    */
    public  void join(String Ip, int port) throws Exception
    {
        chord.joinRing(Ip, port);

        //JsonObject innerObject = new JsonObject();
        //innerObject.addProperty("name", "john");
        //JsonObject fileObj = new JsonObject();
        //fileObj.addProperty("name",fileName);

        //JsonArray fileArray = new JsonArray();
        //fileArray.add(fileObj);

        //JsonObject jsonObject = new JsonObject();
        //jsonObject.add("metadata",fileArray);

        chord.Print();
    }
    

    /**
    * Reads in the metadata 
    * @return JsonReader for the metadata
    */
    public JsonReader readMetaData() throws Exception
    {
        //JsonParser jsonParser _ null;
        long guid = md5("Metadata");
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        InputStream metadataraw = peer.get(guid);
        // jsonParser = Json.createParser(metadataraw);
        JsonReader reader = new JsonReader(new InputStreamReader(metadataraw, "UTF-8"));
        return reader;
    }

    /**
    * Writes the metadata and puts into the Chord
    * @param stream – InputStream of metadata
    */
    public void writeMetaData(InputStream stream) throws Exception
    {
        //JsonParser jsonParser _ null;
        //JsonWriter writer = new JsonWriter(new FileWriter("metadata"));
        long guid = md5("Metadata");
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, stream);
        //writer.beginObject();
        //writer.name("metadata");
        //writer.beginArray();
        //writer.endArray();
        //writer.endObject();
        //writer.close();
    }


    /**
    * Changes a file name in metadata
    * @param oldName – file name to be changed
    * @paran newName – new file name
    */
    public void mv(String oldName, String newName) throws Exception
    {
        // TODO:  Change the name in Metadata
        // Write Metadata
        JsonParser jp = new JsonParser();
        JsonReader jr = readMetaData();
        JsonObject meta = (JsonObject)jp.parse(jr);
        JsonArray fileList = meta.getAsJsonArray("metadata");
        int indexToRemove = -1;

        for (int i=0;i<fileList.size();i++) {
            JsonObject jo = fileList.get(i).getAsJsonObject();
            String name = jo.get("name").getAsString();
            if (name.equals(oldName)) {
                jo.addProperty("name", newName);
                JsonArray pageArray = jo.get("pages").getAsJsonArray();
                
                for (int j=0;j<pageArray.size();j++) {
                    JsonObject page = pageArray.get(j).getAsJsonObject();
                    long guid = md5(newName+(j+1));
                    
                    page.addProperty("guid",guid);
 
                    byte[] content = read(oldName,j+1);
                    ChordMessageInterface peer = chord.locateSuccessor(guid);
                    InputStream is = new FileStream(content);
                    peer.put(guid, is);                   
                }
            }
        }
        String str = meta.toString();
        InputStream is = new FileStream(str.getBytes());
        writeMetaData(is);
    }

    
    /**
    * Lists the file names in metadata
    * @return String of file names
    */
    public String ls() throws Exception
    {
        String listOfFiles = "";
        // TODO: returns all the files in the Metadata
        // JsonParser jp = readMetaData();
        JsonReader jr = readMetaData();
        System.out.println("Start ls");
        jr.beginObject();
        jr.skipValue();
        jr.beginArray();
        while (jr.hasNext()) {
            jr.beginObject();
            while (jr.hasNext()) {
                String name = jr.nextName();
                if (name.equals("name")) {
                    listOfFiles += jr.nextString()+"\n";
                } else {
                    jr.skipValue();
                }
            }
            jr.endObject();
        }
        jr.endArray();
        jr.endObject();
        return listOfFiles;
    }

    /**
    * Creates the file fileName by adding a new entry to the Metadata
    * @param fileName –  new file to be added
    */
    public void touch(String fileName) throws Exception
    {
        
        // TODO: Create the file fileName by adding a new entry to the Metadata
        
        // all this commented out code is how to make a json object

        //JsonObject innerObject = new JsonObject();
        //innerObject.addProperty("name", "john");
        //JsonObject fileObj = new JsonObject();
        //fileObj.addProperty("name",fileName);

        //JsonArray fileArray = new JsonArray();
        //fileArray.add(fileObj);

        //JsonObject jsonObject = new JsonObject();
        //jsonObject.add("metadata",fileArray);

        // this reads existing metadata into a json object

        JsonParser jp = new JsonParser();
        JsonReader jr = readMetaData();
        JsonObject meta = (JsonObject)jp.parse(jr);
        JsonArray fileList = meta.getAsJsonArray("metadata");
        
        JsonObject fileObj = new JsonObject();
        fileObj.addProperty("name",fileName);
        fileObj.addProperty("numberOfPages", 0);
        fileObj.addProperty("pageSize", 1024);
        fileObj.addProperty("size", 0);
        fileObj.add("pages", new JsonArray());

        fileList.add(fileObj);
        
        String str = meta.toString();
        InputStream is = new FileStream(str.getBytes(Charset.forName("UTF-8")));
        writeMetaData(is);

        // Write Metadata        
    }

    
    /**
    * Removes a file in the metadata as well as in Chord
    * @param fileName – name of file to be removed
    */
    public void delete(String fileName) throws Exception
    {
        // TODO: remove all the pages in the entry fileName in the Metadata and then the entry
        // for each page in Metadata.filename
        //     peer = chord.locateSuccessor(page.guid);
        //     peer.delete(page.guid)
        // delete Metadata.filename
        // Write Metadata

        JsonParser jp = new JsonParser();
        JsonReader jr = readMetaData();
        JsonObject meta = (JsonObject)jp.parse(jr);
        JsonArray fileList = meta.getAsJsonArray("metadata");
        int indexToRemove = -1;

        for (int i=0;i<fileList.size();i++) {
            JsonObject jo = fileList.get(i).getAsJsonObject();
            String name = jo.get("name").getAsString();
            if (name.equals(fileName)) {
                JsonArray pageArray = jo.get("pages").getAsJsonArray();
                
                for (int j=0;j<pageArray.size();j++) {
                    JsonObject page = pageArray.get(j).getAsJsonObject();
                    long pageGuid = page.get("guid").getAsLong();

                    ChordMessageInterface peer = chord.locateSuccessor(pageGuid);
                    peer.delete(pageGuid);
                }
                indexToRemove = i;
            }
        }
        if (indexToRemove > -1) {
            fileList.remove(indexToRemove);
            String str = meta.toString();
            InputStream is = new FileStream(str.getBytes());
            writeMetaData(is);
        }
    }
    

    /**
    * Reads the content from a page in a file 
    * @param fileName – name of file to be read from
    * @param pageNumber – page number in the file
    * @return content in byte array 
    */
    public byte[] read(String fileName, int pageNumber) throws Exception
    {
        // TODO: read pageNumber from fileName
        JsonParser jp = new JsonParser();
        JsonReader jr = readMetaData();
        JsonObject meta = (JsonObject)jp.parse(jr);
        JsonArray fileList = meta.getAsJsonArray("metadata");

        byte[] array = null;

        for (int i=0;i<fileList.size();i++) {
            JsonObject jo = fileList.get(i).getAsJsonObject();
            String name = jo.get("name").getAsString();
            if (name.equals(fileName)) {
                JsonArray pageArray = jo.get("pages").getAsJsonArray();
                int index = (pageNumber != -1) ? pageNumber-1 : pageArray.size()-1;
                JsonObject page = pageArray.get(index).getAsJsonObject();
                int size = page.get("size").getAsInt();
                long pageGuid = page.get("guid").getAsLong();

                ChordMessageInterface peer = chord.locateSuccessor(pageGuid);
                InputStream is = peer.get(pageGuid);
                array = new byte[size];

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead = is.read(array, 0, array.length);
                buffer.write(array, 0, nRead);
                buffer.flush();
                is.close();
            }
        }

        return array;
    }
    
    
    public byte[] tail(String fileName) throws Exception
    {
        // TODO: return the last page of the fileName
        return read(fileName,-1);
    }
    public byte[] head(String fileName) throws Exception
    {
        // TODO: return the first page of the fileName
        return read(fileName,1);
    }
    public void append(String filename, byte[] data) throws Exception
    {
        // TODO: append data to fileName. If it is needed, add a new page.
        // Let guid be the last page in Metadata.filename

        JsonParser jp = new JsonParser();
        JsonReader jr = readMetaData();
        JsonObject meta = (JsonObject)jp.parse(jr);
        JsonArray fileList = meta.getAsJsonArray("metadata");

        // loop through list of files
        for (int i=0;i<fileList.size();i++) {
            JsonObject jo = fileList.get(i).getAsJsonObject();
            // get name of file
            String name = jo.get("name").getAsString();
            int num = jo.get("numberOfPages").getAsInt();
            // if name is the filename we're looking for
            if (name.equals(filename)) {
                // get size of page
                int pageSize = jo.get("pageSize").getAsInt();
                // get page list
                JsonArray pageArray = jo.get("pages").getAsJsonArray();
                int startDataAt = 0;
                int pageNum = 1;
                // get last page
                if (num > 0) {
                    int lastPageIndex = pageArray.size()-1;
                    JsonObject pagejo = pageArray.get(lastPageIndex).getAsJsonObject();
                    int size = pagejo.get("size").getAsInt();
                    long guid = pagejo.get("guid").getAsLong();
                    // get page num of newest empty page
                    pageNum = pageArray.size();
                    // if the last page still has room for data
                    if (size < pageSize) {
                        // add data to fill up page
                        //System.out.println("ASAA");
                        //System.out.println(pageSize-size);
                        //System.out.println(data.length);
                        if (pageSize-size < data.length) {
                            //System.out.println("A");
                            startDataAt = pageSize-size;
                        } else {
                            //System.out.println("B");
                            startDataAt = data.length;
                        }
                        //startDataAt = pageSize-size;
                        // get subdata that will be put into page
                        byte[] subdata = Arrays.copyOfRange(data,0,startDataAt);
                        // read what's already in there
                        byte[] existingData = read(filename, lastPageIndex+1);
                        // combine arrays
                        byte[] combinedArray = new byte[subdata.length+existingData.length];
                        for (int j=0;j<existingData.length;j++) {
                            combinedArray[j] = existingData[j];
                        }
                        for (int j=0;j<subdata.length;j++) {
                            combinedArray[j+existingData.length] = subdata[j];
                        }
                        // put into data
                        ChordMessageInterface peer = chord.locateSuccessor(guid);
                        InputStream is = new FileStream(combinedArray);
                        peer.put(guid, is);
                        // change size in metadata
                        pagejo.addProperty("size",combinedArray.length);
                        pageNum += 1;
                    }
                }

                // loop through rest of the data
                //System.out.println("AAAAA");
                //System.out.println(startDataAt);
                //System.out.println(data.length);
                //System.out.println(data.length);
                byte[] restOfData = Arrays.copyOfRange(data, startDataAt, data.length);
                for (int j=startDataAt;j<restOfData.length;j++) {
                    if (j % pageSize == 0) {
                        JsonObject page = new JsonObject();
                        long newGuid = md5(name+pageNum);
                        page.addProperty("number",pageNum);
                        page.addProperty("guid",newGuid);
                        
                        int beginIndex = j;
                        int endIndex = j+pageSize;
                        if (endIndex > restOfData.length) {
                            endIndex = restOfData.length;
                        }
                        byte[] subdata = Arrays.copyOfRange(restOfData, beginIndex, endIndex);
                        page.addProperty("size",subdata.length);

                        ChordMessageInterface peer = chord.locateSuccessor(newGuid);
                        
                        InputStream is = new FileStream(subdata);
                        peer.put(newGuid, is);
                        pageArray.add(page);
                        pageNum += 1;
                    }
                }
                jo.addProperty("numberOfPages",pageNum-1);
            }
        }
        //fileList.get()

        String str = meta.toString();
        InputStream is = new FileStream(str.getBytes());
        writeMetaData(is);
        
        // Write Metadata

        
    }

    public void runMapReduce(File file)
    {
        Counter mapCounter = new Counter();
        Counter reduceCounter = new Counter();
        Counter completedCounter = new Counter();
        
        Mapper mapperReducer = new Mapper();
        //MapInterface mapper = new MapInterface();
        //ReduceInterface reducer = new ReduceInterface();
        
        // map Phases
        //for each page in metafile.file
        //    mapCounter.add(page);
            //Let peer be the process responsible for storing page
        //    peer.mapContext(page, mapper, mapCounter)
        //wait until mapCounter.hasCompleted() = true
        //reduce phase
        //reduceContext(guid, mapperReducer, reduceCounter);
        //wait until reduceCounter.hasCompleted() = true;
        //completed(guid, completedCounter);
        //wait until completedCounter.hasCompleted() 
    }
    
}
