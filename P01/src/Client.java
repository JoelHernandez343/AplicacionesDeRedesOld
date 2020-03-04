import java.io.*;
import java.net.*;

public class Client {

    public static void main(String [] args){

        try {

            FileSender fileSender = new FileSender("localhost", 1234);

            

            fileSender.close();

        } catch (Exception e){

            e.printStackTrace();

        }
        
    }

}