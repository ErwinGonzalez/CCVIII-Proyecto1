import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

class HttpRequestThread implements Runnable {

    private final static Logger logger = Logger.getLogger(ServerThread.class.getName());
    SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM/yyyy hh:mm:ss z");

    /**Este constructor, posiblemente podria recibir la pagina, en lugar de 
     * tenerla hardccoded en el metodo run
     */
    public HttpRequestThread() {

    }

    @Override
    public void run() {

        try {
            /**Importante: No agregar el "http://" al url */
            sendGet("galileo.edu");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void sendGet(String url) throws Exception {

        /*TODO hacer un ciclo, empezar a leer los puertos, leer la primera linea de cada respuesta
          nos interesa encontrar las respuestas que nos den codigo 200 (OK), 301 (Moved Temporarily) o 302(Moved Permantentely)
        */

        /** TODO si se manda un url, con una pagina especifica, por ejemplo galileo.edu/fisicc
         * hay que separar el url de domino de la pagina 
         * y cambiar la estructura del get*/
        String page = "/fisicc";
        String message = "GET "+page+" HTTP/1.0\r\n"+
                         "Host: "+url+"\r\n"+
                         "Accept: */*\r\n"+
                         "Connection: keep-alive\r\n"+
                         "User-Agent: Mozilla/5.0\r\n";
        SocketUtil socketUtil = new SocketUtil(url,80);
        socketUtil.sendGet(message);
        String str;
        //while((str = socketUtil.readOneLine())!=null)
            //System.out.println(str);
            
        //System.out.println(socketUtil.isResponse200());
        /*Socket socket = new Socket(url ,80);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))); 
        out.println(message);
        out.println("\r\n\r\n");
         
        out.flush(); 
        String str;
        while((str= in.readLine())!=null){
                
                System.out.println(str);
               
        }
        socket.close();*/
        
        /* TODO Este codigo iria dentro del loop, para buscar las respuestas
         * De obtener un 301 0 302 hay que buscar como manejar el redirect
         */

        /*Ciclo de lectura del input de la pagina, hay que mejorarlo
         * posiblemente seria buena idea guardar este resultado en un archivo (ej. temp.txt) 
         * para tener donde leer/manipular el resultado
         */
        
       /**Una vez que se tenga el resultado, hay que usar jsoup para crear una lista de tags
        * leer por bloques para poder dar forma al arbol
        * ej.
        *   for(getAllh1)
        *     getAllahref
        *           ...
        * asi, leyendo por tablas/clases, etc
        * aqui mismo, podemos aprovechar a leer la metadata de cada elemento (direccion del salto, nombres de clases, descripciones, etc)
        * eso lo guardamos en una lista, posiblemente un mapa o un hash para poder referenciar de vuelta con la clase
        */

        /** Esta lista, hay que revisar los contenidos, viendo que ,por ejemplo
         * el index, solo deberia salir una vez en el arbol, aun si hay varios saltos al index
         */

        /** Luego se deberia crear un html de respuesta
         * dos columnas, la columna izquierda tendria dos filas
         ***********************************
         *          *                      *
         *          *                      *  
         * Arbol    *    Pagina Web        *
         *          *                      * 
         *          ************************ 
         *          *       Metadata       * 
         ***********************************
         */
    }
}