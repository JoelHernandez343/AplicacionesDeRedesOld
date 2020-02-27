import java.io.*;

public class Client {

    public static void main(String[] args) {

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int port = 1234;
            FileSender fileSender = null;

            while (true){
                System.out.println("Write the ip direction or server name: ");
                String destination = reader.readLine();

                try {
                    fileSender = new FileSender(destination, port);
                } catch (Exception e){
                    System.out.println("No valid direction.");
                }

                if (fileSender != null)
                    break;
            }

            System.out.println("Ready to send files.");

            // File file = new File("client" + File.separator + "test" + File.separator + "file1.txt");
            
            // fileSender.sendFile(file);
            // fileSender.sendFile(file, file.getParent());

            // File path = new File("client/test/son/son2");

            // fileSender.sendDirectory(path, "");

            fileSender.requestTree();

            Thread.sleep(100);
            fileSender.close();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void showFiles(File [] files) {

        for (File file : files){

            if (file.isDirectory())
                System.out.println(File.separator + file.getName());
            else
                System.out.println(file.getName());

        }

    }

}