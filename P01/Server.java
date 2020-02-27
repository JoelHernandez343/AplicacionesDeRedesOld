import java.io.*;
import java.net.*;

public class Server {

    public static String root;
    public static DataInputStream reader;
    public static DataOutputStream writer;
    public static ObjectOutputStream treeSender;

    public static void main(String[] args){

        try {

            root = (new File("")).getAbsolutePath() + File.separator + "server";
            createDirectory("/");

            int port = 1234;
            ServerSocket server = new ServerSocket(port);
            
            System.out.println("Server started at port " + port + ".");

            while (true){
                System.out.println("Waiting for connection...");

                Socket client = server.accept();
                System.out.println("Connected to " + client.getInetAddress().getHostAddress() + ".");

                reader = new DataInputStream(client.getInputStream());
                writer = new DataOutputStream(client.getOutputStream());
                treeSender = new ObjectOutputStream(client.getOutputStream());

                while (true) {
                    
                    int inst = reader.readInt();

                    if (inst == 0)
                        receiveFile();
                    else if (inst == 1)
                        createDirectory();
                    else if (inst == 2)
                        updateDirectoryTree();
                    else 
                        break;
                }

                System.out.println("Disconnecting from " + client.getInetAddress().getHostAddress() + "...");
                
                Thread.sleep(10);
                reader.close();
                client.close();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void receiveFile() throws Exception{

        try {
            String path = reader.readUTF();

            createDirectory(path);
    
            path = root + path;
            
            String name = reader.readUTF();
            long size = reader.readLong();
    
            File file = new File(path + name);
    
            System.out.println("Ready to receive " + name + " of " + size + " bytes...");
    
            DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream(file));
    
            int recv = 0;
    
            while (recv < size){
    
                byte[] buffer = new byte[3000];
                int n = reader.read(buffer);
    
                fileWriter.write(buffer, 0, n);
    
                recv += n;
                int percentage = (int)((recv * 100) / size);
                System.out.print("\r");
                System.out.print("Receiving: " + file.getName() + " [" + percentage + "%]");
    
            }
    
            System.out.print("\r");
            System.out.println("Received: " + file.getName() + " at " + file.getAbsolutePath() + ".");
    
            writer.writeInt(0);
    
            fileWriter.close();
        } catch (Exception e){
            writer.writeInt(1);
        }

    }

    public static void createDirectory() throws Exception{

        try {
            String path = reader.readUTF();
            createDirectory(path);
            System.out.println("Created " + path);

            writer.writeInt(0);
        } catch (Exception e){
            writer.writeInt(1);
        }

    }

    public static boolean createDirectory(String path){
        File file = new File(root + path);
        
        return file.mkdirs();
    }

    public static void updateDirectoryTree() throws IOException{

        System.out.println("Request to send directory tree, building and sending...");

        File home = new File("server");
        FileDescriptor tree = new FileDescriptor(home);
        
        treeSender.writeObject(tree);

        int flag = reader.readInt();
        System.out.println("Sent updated directory tree.");

    }
}