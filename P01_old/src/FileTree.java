import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class represents a single file or a directory tree.
 * @author Joel Hernández
 * @version 1.0.0
 */
public class FileTree implements Serializable {
    
    private static final long serialVersionUID = -7311403979547214572L;
    public String name;
    public boolean isDirectory;
    public ArrayList<FileTree> files;
    public File file;

    /**
     * Create a FileTree with the provided file info.
     * @param file The pointer to the File
     */
    public FileTree(File file){

        name = file.getName();
        isDirectory = file.isDirectory();

        if (isDirectory){
            
            this.file  = null;
            this.files = new ArrayList<FileTree>(0);
            
            for (File f : file.listFiles())
                this.files.add(new FileTree(f));
        
        }
        else {

            this.file  = file; 
            this.files = null;

        }
    }

    /**
     * Show the tree directory in console
     */
    public void show() {

        System.out.println(name);

        if (!isDirectory) return;

        for (FileTree f : files)
            f.show(name);

    }

    /**
     * Internal use to show the directory tree with a relative path
     * @param path The parent's path name
     */
    private void show(String path){

        System.out.println(path + File.separator + name);

        if (!isDirectory) return;

        for (FileTree f : files)
            f.show(path + File.separator + name);

    }

    /**
     * Sort recursively the tree. First the folders, then the names
     */
    public void sortAll(){

        if (!isDirectory)
            return;

        Collections.sort(this.files, FileTree.order);

        for (FileTree f : this.files)
            f.sortAll();

    }

    /**
     * Comparator to sort
     */
    private static Comparator<FileTree> order = new Comparator<FileTree>() {
    
        @Override
        public int compare(FileTree a, FileTree b){

            if (a.isDirectory == b.isDirectory)
                return a.name.compareTo(b.name);
            
            return a.isDirectory ? -1 : 1;

        }
    
    };

}