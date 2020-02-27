import javax.swing.JFileChooser;
import java.net.*;
import java.io.*;

public class Envia{
    public static void main(String [] args){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            int pto = 5678;
            InetAddress dst = null;

            while(true){
                System.out.println("Escribe la direccin o nombre calificado del servidor");
                String host = br.readLine();
                try{
                    dst = InetAddress.getByName(host);

                }catch(Exception u){
                    System.err.println("Direccion no valida");
                    continue;
                }
                if(dst != null){
                    break;
                }
            }
            Socket cl = new Socket(dst,pto);
            System.out.println("Conexion con servidor establecida... mostrando caja de seleccin");
            JFileChooser jf = new JFileChooser();
            int r = jf.showOpenDialog(null);
            //jf.requestFocus();
            if(r == JFileChooser.APPROVE_OPTION){
                File f = jf.getSelectedFile();
                String nombre = f.getName();
                long tam = f.length();

                String ruta = f.getAbsolutePath();
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(ruta));

                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                int n,porcentaje;
                long env = 0;

                while(env < tam){
                    byte []b = new byte[3000];
                    n = dis.read(b);
                    dos.write(b,0,n);
                    dos.flush();
                    env = env + n;
                    porcentaje = (int)((env * 100)/tam);
                    System.out.println("\n Se ha enviado el "+porcentaje+"% del archivo "+ruta);

                }
                System.out.println("Archivo Enviado ");
                dis.close();
                dos.close();
                cl.close();
                br.close();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}