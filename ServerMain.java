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
        while (true){
            /*ServerThread thread = new ServerThread(serverSocket.accept());
            tpe.submit(thread);*/
            tpe.submit(new HttpRequestThread());
        }
    }
}