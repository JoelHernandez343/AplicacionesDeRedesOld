//CHMF
import java.net.*;
import java.io.*;

public class C_Eco{
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
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutuputStream()));
            BufferedReader br1 = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                while(true){
                    System.out.println("Escribe una cadena <Enter> para enviar, o \"cerrar\" para terminar ");
                    String msj = br.readLine();
                    pw.println(msj);
                    pw.flush();
                    if(msg.composeToIgnoreCase("cerrar") == 0){
                        System.out.println("Termina Programa");
                        br.close();
                        br1.close();
                        pw.close();
                        cl.close();
                        System.exit(0);
                    }else{
                        String eco = br1.readLine();
                        System.out.println("Eco recibido "+eco);
                    }
                }
		}catch(Exception e){
            e.printStackTracer();
        }
	
	}


} 