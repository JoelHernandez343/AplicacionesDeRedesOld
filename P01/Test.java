import java.io.*;

public class Test {

    public static void main(String [] args){

        File file = new File("client");
        FileDescriptor tree = new FileDescriptor(file);
        tree.showTree();

    }

}