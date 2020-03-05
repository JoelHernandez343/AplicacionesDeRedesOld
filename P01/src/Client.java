import java.io.*;

public class Client {

    public static void main(String [] args){

        try {

            FileSender fileSender = new FileSender("localhost", 1234);
            

            fileSender.deleteRemote("/client");

            // FileTree tree = new FileTree(new File("client"));
            
            // fileSender.send(tree);

            fileSender.close();

        } catch (Exception e){

            e.printStackTrace();

        }
        
    }

}