import java.io.*;
import java.net.*;


/**
 * This class represents a file sender through stream {@link java.net.Socket}
 * with a basic handmade protocol:
 * <blockquote>
 * <p> 0: Send file </p>
 * <p> 1: Create Directory </p>
 * <p> 2: Get directory tree </p>
 * <p> -1: Close connection </p>
 * </blockquote>
 * There is no information sent by server and no information about errors.
 * 
 * @author Joel Hernández
 * @version 0.1.0
 */
public class FileSender {

    /**
     * {@code Socket} connected to the server.
     */
    private Socket connection;

    /**
     * {@code DataOutputStream} that send the information.
     */
    private DataOutputStream sender;
    private DataInputStream receiver;

    /**
     * Create a FileSender with a server name and the server port.
     * @param destination the server name
     * @param port ther server port
     * @throws Exception Thrown by {@code Socket}
     */
    public FileSender(String destination, int port) throws Exception{
        InetAddress dest = InetAddress.getByName(destination);
        
        this.connection = new Socket(dest, port);
        this.sender = new DataOutputStream(this.connection.getOutputStream());
        this.receiver = new DataInputStream(this.connection.getInputStream());

    }

    /**
     * Close the connection between the server and the client and close
     * the {@code Socket} and the {@code DataOutputstream} openned in the
     * {@code FileSender} creation.
     * @throws IOException Thrown by the {@code close()} methods.
     */
    public void close() throws IOException{
        sender.writeInt(-1);

        this.sender.close();
        this.receiver.close();
        this.connection.close();
    }

    /**
     * Send the {@code File} to the server
     * @param file the file to send
     * @throws IllegalArgumentException If the {@code File} provided is a directory
     * @throws Exception Thrown by the internal call to {@link FileSender#sendFile(File, String)}
     */
    public void sendFile(File file) throws IllegalArgumentException, Exception{
        if (file.isDirectory())
            throw new IllegalArgumentException("The file provided is a directory");
        
        sendFile(file, "");
    }

    /**
     * Send the {@code File} to the server with relative {@code path} as destination
     * @param file the file to send
     * @param path the relative path
     * @throws IllegalArgumentException If the {@code File} provided is a directory
     * @throws IOException Thrown by the {@code fileReader} and {@link FileSender#sender} operations
     */
    public void sendFile(File file, String path) throws IllegalArgumentException, IOException{
        if (file.isDirectory())
            throw new IllegalArgumentException("The file provided is a directory");
        
        DataInputStream fileReader = new DataInputStream(new FileInputStream(file));

        if (path.compareTo("") == 0)
            path = File.separator;
        if (path.charAt(0) != File.separatorChar)
            path = File.separator + path;
        if (path.charAt(path.length() - 1) != File.separatorChar)
            path += File.separator;

        sender.writeInt(0);
        sender.flush();
        sender.writeUTF(path);
        sender.flush();
        sender.writeUTF(file.getName());
        sender.flush();
        sender.writeLong(file.length());
        sender.flush();

        int sent = 0;

        while (sent < file.length()){

            byte[] buffer = new byte[3000];
            int n = fileReader.read(buffer);

            sender.write(buffer, 0, n);
            sender.flush();

            sent += n;
            int percentage = (int)((sent * 100) / file.length());
            System.out.print("\r");
            System.out.print("Sending: " + file.getName() + " [" + percentage + "%]");

        }

        int error = receiver.readInt();
        
        System.out.print("\r");

        if (error == 0)
            System.out.println("Sent: " + file.getName() + ".");
        else 
            System.out.println("Error to send: " + file.getName() + ".");

        
        fileReader.close();

    }

    public void sendDirectory(File path) throws IllegalArgumentException, IOException{

        if (!path.isDirectory())
            throw new IllegalArgumentException("The path provided is a file.");

        createRemoteDirectory(path.getPath());

        File [] files = path.listFiles();

        for (File file : files){

            if (!file.isDirectory()){
                sendFile(file, file.getParent());
                continue;
            }
            
            sendDirectory(file);

        }

    }

    public void sendDirectory(File path, String destination) throws IllegalArgumentException, IOException{

        if (!path.isDirectory())
            throw new IllegalArgumentException("The path provided is a file.");

        createRemoteDirectory(destination + File.separator + path.getName());

        File [] files = path.listFiles();

        for (File file : files){

            if (!file.isDirectory()){
                sendFile(file, destination + File.separator + path.getName() + File.separator);
                continue;
            }
            
            sendDirectory(file, destination + File.separator + path.getName() + File.separator);

        }

    }

    private void createRemoteDirectory(String path) throws IOException{
        
        if (path.compareTo("") == 0)
            path = File.separator;
        if (path.charAt(0) != File.separatorChar)
            path = File.separator + path;
        if (path.charAt(path.length() - 1) != File.separatorChar)
            path += File.separator;

        sender.writeInt(1);
        sender.flush();
        sender.writeUTF(path);
        sender.flush();

        int error = receiver.readInt();

        if (error == 0)
            System.out.println("Created: " + path);
        else
            System.out.println("Error creating: " + path);

    }

    public FileDescriptor requestTree() throws Exception{

        sender.writeInt(2);
        sender.flush();

        ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
        Object o = ois.readObject();
        FileDescriptor tree = null;

        if (o instanceof FileDescriptor){

            tree = (FileDescriptor)o;
            tree.showTree();

        } else {

            throw new Exception("Cannot get the directory tree from server.");

        }

        sender.writeInt(0);
        sender.flush();

        return tree;

    }

}