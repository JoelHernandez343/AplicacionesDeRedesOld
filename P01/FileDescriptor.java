import java.io.*;
import java.util.ArrayList;

public class FileDescriptor implements Serializable {

    private static final long serialVersionUID = -6369640401439908869L;

    public String name;
    public boolean isDirectory;
    public String relativePath;
    public ArrayList<FileDescriptor> files;

    public FileDescriptor(File file){

        name = file.getName();
        isDirectory = file.isDirectory();
        relativePath = File.separator;

        if (isDirectory){

            files = new ArrayList<FileDescriptor>(0);
            fill(file.listFiles());

        } else {
            files = null;
        }

    }

    public FileDescriptor(File file, String relativePath){

        name = file.getName();
        isDirectory = file.isDirectory();
        this.relativePath = relativePath;

        if (isDirectory){

            files = new ArrayList<FileDescriptor>(0);
            fill(file.listFiles());

        } else {
            files = null;
        }

    }

    private void fill(File [] files){

        for (File file : files){

            FileDescriptor fd;

            if (file.isDirectory())
                fd = new FileDescriptor(file, this.relativePath + this.name + File.separator);
            else
                fd = new FileDescriptor(file, this.relativePath + this.name + File.separator);

            this.files.add(fd);

        }

    }

    public void showTree(){
        System.out.println(relativePath + name);

        if (!isDirectory)
            return;
        
        for (FileDescriptor file : files){
            file.showTree();
        }
    }

}