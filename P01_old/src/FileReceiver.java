import java.io.*;
import java.net.*;

/**
 * This class represents a file receiver through stream {@link java.net.Socket}
 * with a basic handmade protocol. The messages are defined in enum {@link FSP}.
 * @author Joel Hernández
 * @version 0.5.0
 */
public class FileReceiver {

    private Socket client;
    private ObjectOutputStream sender;
    private ObjectInputStream receiver;
    public FSP status;
    String root;

    /**
     * Create a {@link FileReceiver}
     * @param client connected host
     * @param root the server path
     * @throws IOException thrown by the {@code Streams}
     */
    public FileReceiver(Socket client, String root) throws IOException{

        this.root = root;

        this.client = client;
        this.receiver    = new ObjectInputStream(this.client.getInputStream());
        this.sender      = new ObjectOutputStream(this.client.getOutputStream());

    }

    /**
     * Close all connections
     * @throws Exception thrown by the {@code Streams}
     */
    public void close() throws Exception{

        this.receiver.close();
        this.sender.close();
        this.client.close();

    }

    /**
     * Receives a request from the client
     * @throws Exception thrown by {@link #msgReceiver}
     */
    public void accept() throws Exception{

        status = (FSP)receiver.readObject();

    }

    /**
     * Receives a file from the client
     * @throws Exception thrown by {@link #sender}
     */
    public void receiveFile() throws Exception{

        try {
            
            String path = root + receiver.readUTF();

            mkDir(path);

            String name = receiver.readUTF();
            long size   = receiver.readLong();

            File file = new File (path + name);

            System.out.println("Ready to receive " + name + " of " + size + " bytes...");

            DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream(file));

            long rcv = 0;

            while (rcv < size){

                byte[] buffer = new byte[3000];
                int n = receiver.read(buffer);

                fileWriter.write(buffer, 0, n);

                rcv += n;
                int percentage = (int)((rcv * 100) / size);
                System.out.print("\r");
                System.out.print("Receiving:" + file.getName() + " [" + percentage + "%]");

            }

            System.out.print("\r");
            System.out.println("Received: " + file.getName() + " at " + path);
        
            status = FSP.S_SUCCESS;
            sender.writeObject(status);

            fileWriter.close();

        } catch (Exception e){

            System.out.println("Error receiving file.");

            status = FSP.E_ERROR;
            sender.writeObject(status);

        }

    }

    /**
     * Create a directory
     * @throws Exception thrown by {@link #sender}
     */
    public void createDirectory() throws Exception{

        try {

            String path = root + receiver.readUTF();
            mkDir(path);
            System.out.println("Created " + path);

            status = FSP.S_SUCCESS;
            sender.writeObject(status);

        } catch (Exception e){

            System.out.println("Error creating directory.");

            status = FSP.E_ERROR;
            sender.writeObject(status);

        }

    }

    /**
     * Create the path in the file system
     * @param path to create
     * @return the result of {@link File#mkdirs()}
     */
    public static boolean mkDir(String path) {

        File file = new File(path);
        return file.mkdirs();

    }

    /**
     * Create a {@link FileTree} representation from the root of the server and send it
     * @throws Exception thrown by the {@code Streams}
     */
    public void sendTreeDirectory() throws Exception{

        System.out.println("Request to send directory tree, building and sending...");

        File file = new File(root);
        FileTree tree = new FileTree(file);

        sender.writeObject(tree);

        System.out.println("Sent updated directory tree.");

    }

    /**
     * Delete a file
     * @throws Exception thrown by {@link #sender}
     */
    public void deleteFile() throws Exception{

        String path = root + receiver.readUTF();

        try {
            
            File file = new File(path);
            
            if (file.isDirectory())
                deleteFiles(file.listFiles());

            if (file.delete()){

                System.out.println("Removed: " + path);

                status = FSP.S_SUCCESS;
                sender.writeObject(status);

            } else {

                throw new Exception("");

            }

        } catch (Exception e){
    
            System.out.println("Error deleting directory: " + path);

            status = FSP.E_ERROR;
            sender.writeObject(status);

        }

    }

    private void deleteFiles(File[] files){

        for (File file : files){

            if (!file.isDirectory()){

                if (file.delete())
                    System.out.println("Removed: " + file.getAbsolutePath());
                else
                    System.out.println("Cannot remove: " + file.getAbsolutePath());

                continue;
            }

            deleteFiles(file.listFiles());

            if (file.delete())
                System.out.println("Removed: " + file.getAbsolutePath());
            else
                System.out.println("Cannot remove: " + file.getAbsolutePath());

        }

    }

}