import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.nio.file.*;

/**
* @author Amy Yang
* @author Tiler Dao
* @author Christian Eirik Blydt-Hansen
* This class represents a client that accesses the Chord distributed file system.
*/
public class Client {

    /**distributed file system object*/
    DFS dfs;


    /**
    * Constructor initializes DFS object and runs the program
    * @param port – port number that the client is using
    */
    public Client(int port) throws Exception {
        dfs = new DFS(port);
        run();
    }

    /**
    * Runs the DFS and Chord program
    */
    public void run() throws Exception {
        Scanner in = new Scanner(System.in);

        boolean isRunning = true;
        byte[] result;
        String name;
        String oldName;
        int pageNum;

        while(isRunning==true) {
        	System.out.println();
            System.out.println("\nEnter a command");
            System.out.println("1. join");
            System.out.println("2. ls");
            System.out.println("3. touch");
            System.out.println("4. delete");
            System.out.println("5. read");
            System.out.println("6. tail");
            System.out.println("7. head");
            System.out.println("8. append");
            System.out.println("9. move");
            System.out.println("10. map reduce");
            System.out.println("0. quit");

            String choice = in.nextLine();

            try
            {
                switch(choice) {
                    case "1":
                        System.out.println("Please enter port");
                        String port = in.nextLine();
                        dfs.join("localhost",Integer.parseInt(port));
                        //join
                        break;
                    case "2":
                        //ls
                        System.out.println(dfs.ls());
                        break;
                    case "3":
                        //touch
                        System.out.println("Please enter file name");
                        name = in.nextLine();
                        dfs.touch(name);
                        break;
                    case "4":
                        //delete
                        System.out.println("Please enter file name");
                        name = in.nextLine();
                        dfs.delete(name);
                        break;
                    case "5":
                        //read
                        System.out.println("Please enter file name");
                        name = in.nextLine();
                        System.out.println("Please enter page number");
                        String pageString = in.nextLine();
                        pageNum = 1;
                        try
                        {
                            pageNum = Integer.parseInt(pageString);
                        } catch (Exception e)
                        {
                            System.out.println("Invalid page number");
                            break;
                        }
                        result = dfs.read(name,pageNum);
                        System.out.println((new String(result)).replace("/n","\n"));
                        break;
                    case "6":
                        //tail
                        System.out.println("Please enter file name");
                        name = in.nextLine();
                        result = dfs.tail(name);
                        System.out.println(new String(result).replace("/n","\n"));
                        break;
                    case "7":
                        //head
                        System.out.println("Please enter file name");
                        name = in.nextLine();
                        result = dfs.head(name);
                        System.out.println(new String(result).replace("/n","\n"));
                        break;
                    case "8":
                        //append
                        System.out.println("Please enter file name");
                        name = in.nextLine();
                        System.out.println("Please enter file content");
                        String content = in.nextLine();
                        Scanner sc = new Scanner(new File(content));
                        StringBuilder sb = new StringBuilder();
                        while(sc.hasNextLine())
                        {
                            sb.append(sc.nextLine() + "/n");
                        }
                        content = sb.toString();
                        byte[] b = content.getBytes();
                        dfs.append(name,b);
                        break;
                    case "9":
                        //move
                        System.out.println("Please enter file name you want to change");
                        String old_Name = in.nextLine();
                        System.out.println("Please enter a new file name");
                        String newName = in.nextLine();
                        dfs.mv(old_Name,newName);
                        break;
                    case "10":
                    	System.out.println("Enter file name");
                    	String fileName = in.next();
                    	dfs.runMapReduce(fileName);
                    	break;
                    case "0":
                        //quit
                        
                        //isRunning = false;
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid input");
                }
            } catch (Exception e)
            {
                System.out.println(e);
                System.out.println("Invalid input");
            }
        }
      //  System.out.println("out");
    }
    
    static public void main(String args[]) throws Exception {
        if (args.length < 1 ) {
            throw new IllegalArgumentException("Parameter: <port>");
        }
        Client client=new Client( Integer.parseInt(args[0]));

     } 
}
