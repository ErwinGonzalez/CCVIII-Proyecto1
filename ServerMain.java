import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class ServerMain {
    public static void main (String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(2407);
        BufferedReader readFile = new BufferedReader(new FileReader("./conf.txt"));
        String readLine = readFile.readLine();
        String[] maxThreadsConf = readLine.split("=");
        int maxThreadsNumber = 0;
        //TODO el programa no termina, averiguar porque

        if(maxThreadsConf.length == 2 && maxThreadsConf[0].equals("MaxThreads")){
            try{
                maxThreadsNumber = Integer.parseInt(maxThreadsConf[1]);
            }catch(NumberFormatException nfe){
                System.err.println(nfe.getMessage());
            }
        }else{
            System.err.println("Error in configuration file");
        }

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreadsNumber);
        /** TODO temporalmente deshabilitar el while, causa que se llame siempre el mismo thread,
         * arreglar luego
        */
        //while (true){
            /*ServerThread thread = new ServerThread(serverSocket.accept());
            tpe.submit(thread);*/
            //TODO aqui se podria mandar a llamar la thread con un socket, asi en el url va el parametro a buscar
            tpe.submit(new HttpRequestThread());
        //}
        readFile.close();
        serverSocket.close();

    }
}