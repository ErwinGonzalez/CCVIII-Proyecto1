import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class ServerMain {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(2407);
        BufferedReader readFile = new BufferedReader(new FileReader("./conf.txt"));
        String readLine = readFile.readLine();
        String[] maxThreadsConf = readLine.split("=");
        int maxThreadsNumber = 0;
        //TODO el programa no termina, averiguar porque

        if (maxThreadsConf.length == 2 && maxThreadsConf[0].equals("MaxThreads")) {
            try {
                maxThreadsNumber = Integer.parseInt(maxThreadsConf[1]);
            } catch (NumberFormatException nfe) {
                System.err.println(nfe.getMessage());
            }
        } else {
            System.err.println("Error in configuration file");
        }

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreadsNumber);

        try {
            while (true) {
                //se llama a esta pagina como http://localhost:2407/${url}
                tpe.submit(new HttpRequestThread(serverSocket.accept()));
            }
        }finally {
            readFile.close();
            serverSocket.close();
        }
    }
}