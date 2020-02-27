import java.io.*;
import java.net.*;

public class Recibe{
    public static void main(String[] args) {
        try {
            int pto = 5678;
            ServerSocket s = new ServerSocket(pto);
            System.out.println("Servidor iniciado en el puerto "+pto+" Esperando archivo...");
            for(;;){
                Socket cl = s.accept();
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String nombre = dis.readUTF();
                long tam = dis.readLong();
                System.out.println("Preparado para recibir el archivo "+nombre+" de "+tam+" bytes desde "+cl.getInetAddress()+": "+cl.getPort());

                DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                long rec = 0;
                int n = 0, porcentaje = 0;

                while(rec < tam){
                    byte[] b = new byte[3000];//para correr en produccion en 15000
                    n = dis.read(b);
                    dos.write(b,0,n);//escribo buffer partiendo de la posicion 0
                    dos.flush();
                    rec = rec + n;
                    porcentaje = (int)((rec * 100)/ tam);
                    System.out.println("\r Recibio el "+porcentaje+"% del archivo");//retorno de carro

                }

                File f = new File("");
                String dst = f.getAbsolutePath();
                System.out.println("Archivo recibido y descargado en la carpeta "+dst);
                
                dos.close();
                dis.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}