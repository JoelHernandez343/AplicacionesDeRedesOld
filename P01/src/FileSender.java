import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class represents a file sender through stream {@link java.net.Socket}
 * with a basic handmade protocol. The messages are defined in enum {@link FSP}.
 * @author Joel Hernández
 * @version 0.5.0
 */
public class FileSender {

    private Socket connection;
    private DataOutputStream sender;
    private ObjectInputStream receiver;
    private ObjectOutputStream msgSender;

    /**
     * Create a {@code FileSender}
     * @param server the server name
     * @param port the server port
     * @throws UnknownHostException thrown by {@link InetAddress}
     * @throws IOException thrown by the {@code Stream} 
     */
    public FileSender(String server, int port) throws UnknownHostException, IOException{

        InetAddress dest = InetAddress.getByName(server);

        this.connection = new Socket(dest, port);
        this.sender     = new DataOutputStream(this.connection.getOutputStream());
        this.receiver   = new ObjectInputStream(this.connection.getInputStream());
        this.msgSender  = new ObjectOutputStream(this.connection.getOutputStream());

    }

    /**
     * Close all connections 
     * @throws IOException thrown by {@code close()} methods 
     */
    public void close() throws IOException{

        msgSender.writeObject(FSP.C_CLOSE_CONECCTION);

        this.sender.close();
        this.receiver.close();
        this.msgSender.close();
        this.connection.close();

    }

    /**
     * Send a {@link FileTree} to the root path of the server
     * @param file the file tree, may be a file or a directory
     * @throws Exception thrown by {@link #send(FileTree file, String path)}
     */
    public void send(FileTree file) throws Exception{

        send(file, File.separator);

    }
    
    /**
     * Send a {@link FileTree}
     * @param file the file tree, may be a file or a directory
     * @param path the relative destination
     * @throws Exception thrown by {@link #sendFile(File file, String path)} and others
     */
    public void send(FileTree file, String path) throws Exception{

        path = validPath(path);

        if (!file.isDirectory){
            sendFile(file.file, path);
            return;
        }

        createDirectory(path + file.name);
        send(file.files, path + file.name);

    }

    /**
     * Send recursively a {@link FileTree}
     * @param files the children of the {@link FileTree}
     * @param path the destination
     * @throws Exception thrown by many methods
     */
    private void send(ArrayList<FileTree> files, String path) throws Exception{

        path = validPath(path);

        for (FileTree f : files){

            if (!f.isDirectory){

                sendFile(f.file, path);
                continue;

            }

            createDirectory(path + f.name);
            send(f.files, path + f.name);

        }

    }

    /**
     * Create a directory in the server
     * @param path the path to create
     * @throws Exception thrown by many methods
     */
    private void createDirectory(String path) throws Exception{

        msgSender.writeObject(FSP.R_CREATE_DIRECTORY);
        msgSender.flush();

        sender.writeUTF(path);
        sender.flush();

        FSP report = (FSP)receiver.readObject();

        if (report.compareTo(FSP.S_SUCCESS) == 0)
            System.out.println("Created: " + path);
        else
            System.out.println("Error creating: " + path);

    }

    /**
     * Send a single {@link File} to the server
     * @param file that will be sent
     * @param path the destination
     * @throws IOException thrown by the {@code Streams}
     * @throws ClassNotFoundException thrown by the internal casting
     */
    private void sendFile(File file, String path) throws IOException, ClassNotFoundException{

        msgSender.writeObject(FSP.R_SENT_FILE);
        msgSender.flush();

        DataInputStream fileReader = new DataInputStream(new FileInputStream(file));

        sender.writeUTF(path);
        sender.flush();
        sender.writeUTF(file.getName());
        sender.flush();
        sender.writeLong(file.length());
        sender.flush();

        long sent = 0;

        while (sent < file.length()){

            byte[] buffer = new byte[3000];
            int n = fileReader.read(buffer);

            sender.write(buffer, 0, n);
            sender.flush();

            sent += n;
            int percentage = (int)((sent * 100) / file.length());
            System.out.print("\r");
            System.out.print("Sending: " + file.getName() + " [" + percentage +"%]");

        }
        System.out.print("\r");

        FSP report = (FSP)receiver.readObject();

        if (report.compareTo(FSP.S_SUCCESS) == 0)
            System.out.println("Sent " + file.getName() + ".");
        else
            System.out.println("Error sending: " + file.getName() + " to " + path);

        fileReader.close();

    }

    /**
     * Create a valid path
     * @param path the original path
     * @return a valid path
     */
    public static String validPath(String path){

        String r = new String(path);
        if (r.compareTo("") == 0)
            r = File.separator;
        if (r.charAt(0) != File.separatorChar)
            r = File.separator + r;
        if (r.charAt(r.length() - 1) != File.separatorChar)
            r += File.separator;
        
        return r;

    }

    /**
     * Get the {@link FileTree} representation of the server
     * @return the {@link FileTree}
     * @throws Exception thrown by the {@code Streams}
     */
    public FileTree getRemoteTree() throws Exception{

        msgSender.writeObject(FSP.R_TREE_DIRECTORY);
        msgSender.flush();

        return (FileTree)receiver.readObject();

    }

    /**
     * Delete a remote file / directory
     * @param path the file / directory to remove
     * @throws Exception thrown by the {@code Streams}
     */
    public void deleteRemote(String path) throws Exception{

        msgSender.writeObject(FSP.R_REMOVE);
        msgSender.flush();

        sender.writeUTF(path);
        sender.flush();

        FSP report = (FSP)receiver.readObject();
        
        if (report.compareTo(FSP.S_SUCCESS) == 0)
            System.out.println("Removed: " + path);
        else
            System.out.println("Cannot remove: " + path);

    }

}