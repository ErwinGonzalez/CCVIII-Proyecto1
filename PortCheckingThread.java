import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class PortCheckingThread implements Callable<boolean[]> {

      int minPort,maxPort,size;
      String url;
    PortCheckingThread(String baseUrl, int min, int max){
        minPort = min;
        maxPort = max;
        //correccion del tamaño, del 0 al 9,999, hay 10,000 puertos
        size = max-min+1;
        url = baseUrl;
    }

    @Override
    public boolean[] call() {
        boolean[] openPorts = new boolean[size];
        for(int i = 0;i<size;i++){
            openPorts[i] = isPortOpen(url,minPort+i);
        }
        return openPorts;
    }

    private boolean isPortOpen(String url,int port){
        Socket s = new Socket();
        try{
            //timeout para no esperar al timeout standar
            System.out.println(url+" testing port: "+port);
            s.connect(new InetSocketAddress(url,port));
            return true;
        }catch(Exception e){
            return false;
        }finally {
            if(s!=null){
                try{s.close();}
                catch (Exception e){}
            }
        }
    }

}
