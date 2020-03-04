import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args){

        try {

            int port = 1234;
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started at port " + port + ".");

            String root = (new File("")).getAbsolutePath() + File.separator + "server";
            FileReceiver.mkDir(root);

            while (true) {

                System.out.println("Waiting for connection...");

                Socket client = server.accept();
                System.out.println("Connected to " + client.getInetAddress().getHostAddress());

                FileReceiver receiver = new FileReceiver(client, root);

                while (true){

                    receiver.accept();

                    switch (receiver.status){

                        case R_SENT_FILE:
                            receiver.receiveFile();
                        case R_CREATE_DIRECTORY:
                            receiver.createDirectory();
                        case R_TREE_DIRECTORY:
                            receiver.sendTreeDirectory();
                        case R_REMOVE:
                            receiver.deleteFile();
                        default:
                            break;

                    }

                    if (receiver.status.compareTo(FSP.C_CLOSE_CONECCTION) == 0)
                        break;

                }

                receiver.close();

            }

        } catch (Exception e){

            e.printStackTrace();

        }

    }

}