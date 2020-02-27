//CHMF
import java.net.*;
import java.io.*;

public class CHMF{
	public static void main(String [] args){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int pto = 1234;
			InetAdress dst = null;
			
			while(true){
				System.out.println("Escribe la dirección o nombre calificado del servidor");
				String host = br.readLine();
				try{
                    dst = InetAdress.getByName(host);
                    
				}catch(UncaugthException u){
                    System.out.println("Dirección no válida");
                    continue;
                }
                if(dst != null)break;
			}

            Socket cl = new Socket(dst,pto);
            System.out.println("Conexión con el servidor establecida, recibiendo mensaje");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));

            BufferedReader br1 = new BufferedReader(new InputStreamReader(cl.getInputStream()));
            String msj = br1.readLine();
            System.out.println("Mensaje Recibido:"+msj+"\n devolviendo saludo");
            pw.println("Saludo devuelto");

            pw.flush();
            System.out.println("Terminando aplicación");
            br.close();
            br1.close();
            pw.close();
            cl.close();

		}catch(Exception e){
            e.printStackTracer();
        }
	
	}


} 