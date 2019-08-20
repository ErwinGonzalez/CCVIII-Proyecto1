import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class ServerMain {
    /*TODO, en esta clase, se debe de agregar el otro tipo de thread para revisar los puertos
    *  aqui, se podria agregar una lista
    *   boolean[65535] ports;
    *  esta lista se pasa a ambos tipos de threads, asi podemos detener el funcionamiento de
    *  la que pide la pagina hasta que la lista contenga un 'true'
    *  el otro tipo de thread se puede pasar una cierta cantidad de puertos
    *  i.e. de 0..1000,1001..2000, etc, esos serian los puertos que revisan y las posiciones que tienen
    *  que llenar en la lista*/
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
                tpe.submit(new htmlOnClickTesting(serverSocket.accept()));
            }
        }finally {
            readFile.close();
            serverSocket.close();
        }
    }
}