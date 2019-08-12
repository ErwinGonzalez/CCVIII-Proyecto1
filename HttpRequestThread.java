import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileReader;
import java.util.Date;



class HttpRequestThread implements Runnable {

    private final static Logger logger = Logger.getLogger(ServerThread.class.getName());
    SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM/yyyy hh:mm:ss z");
    Socket serverRequest;
    BufferedReader reader;
    DataOutputStream output;

    /**
     * Este constructor, posiblemente podria recibir la pagina, en lugar de tenerla
     * hardccoded en el metodo run
     */
    public HttpRequestThread() {

    }

    public HttpRequestThread(Socket serverConnection) {
        try {
            this.serverRequest = serverConnection;
            this.reader = new BufferedReader(new InputStreamReader(serverRequest.getInputStream()));
            this.output = new DataOutputStream(serverRequest.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            String request = "";
            request = reader.readLine();

            if (request != null && !request.isEmpty()) {
                // System.out.println(request);
                String[] requestType = request.split(" ");
                sendGet(requestType[1].substring(1));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean isPortOpen(String url,int port){
        Socket s = new Socket();
        try{
            //timeout para no esperar al timeout standar
            s.connect(new InetSocketAddress(url,port),3000);
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
    private void sendGet(String url) throws Exception {

        /*
         * TODO hacer un ciclo, empezar a leer los puertos, leer la primera linea de
         * cada respuesta nos interesa encontrar las respuestas que nos den codigo 200
         * (OK), 301 (Moved Temporarily) o 302(Moved Permantentely)
         */
    //Open Ports checker
        int portNumber = 78;
        int portMaxNumber = 83;

        String urlString = url;
        if (urlString.contains("http://"))
            urlString = urlString.replace("http://", "");
        if (url.contains("https://"))
            urlString = urlString.replace("https://", "");
        //urlString = urlString.contains("/") ? urlString.substring(0, urlString.indexOf("/")):urlString;
        boolean portOpen = false;

        System.out.println(urlString);
        ArrayList<Integer> openPorts = new ArrayList<>();
        for(int port = portNumber;port<=portMaxNumber;port++) {
            if(isPortOpen(urlString,port) && !portOpen) {
                portOpen = true;
                portNumber = port;
                openPorts.add(portNumber);
            }
           // System.out.println("isPortOpen: "+portOpen+" portNumber: "+port);
        }
        System.out.println(openPorts.toString());

        //

        SocketUtil socketUtil = new SocketUtil(urlString, portNumber);
        socketUtil.sendGet(url);
        String str;
        ArrayList<String> list = new ArrayList<>();
        String htmlRead = "";

        while ((str = socketUtil.readLineIfNotEmpty()) != null) {
            // TODO esta leyendo un 0/null de mas
            // System.out.println(str);
            htmlRead += str + "\n";
        }

        // TODO add other stuff a[href], and other links
        Document doc = Jsoup.parse(htmlRead);
        Elements alinks = doc.select("a[href]");
        //TODO button might need to display text or smthng
        Elements btnlinks = doc.getElementsByTag("button");
        Elements formlinks = doc.getElementsByTag("action");
        String returnString = "<html><body>";

        for (Element link : alinks)
            list.add("<a href=http://localhost:2407/"+link.attr("href")+"> a[href]"+link.attr("href")+"</a>");
            // System.out.println(link);
            //returnString += "<a href="+link.attr("href")+">"+link.attr("href")+"</a><br>\n";
        for (Element link : btnlinks)
            list.add("<a href=http://localhost:2407/"+link.attr("onClick")+"> btn[onClick]"+link.attr("onClick")+"</a>");
        for (Element link : formlinks)
            list.add("<a href=http://localhost:2407/"+link.attr("action")+"> form[action]"+link.attr("action")+"</a>");

        IndexItem item = new IndexItem(doc.title(),url,list,"");
        returnString += item.formattedHTML()+"</body></html>";

        String headerResponse = "HTTP/1.1 200 OK" + "Server : TestServer\n" + "Date: "
                + format.format(new Date(System.currentTimeMillis())) + "\n" + "Content-Type: text/html" + "\n"
                + "Connection: keep-alive\n" + "Content-Length: " + returnString.length() + "\r\n";

        output.writeBytes(headerResponse);
        output.writeBytes("\r\n");
        output.writeBytes(returnString);
        /*
         * Socket socket = new Socket(url ,80); BufferedReader in = new
         * BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter
         * out = new PrintWriter(new BufferedWriter(new
         * OutputStreamWriter(socket.getOutputStream()))); out.println(message);
         * out.println("\r\n\r\n");
         * 
         * out.flush(); String str; while((str= in.readLine())!=null){
         * 
         * System.out.println(str);
         * 
         * } socket.close();
         */

        /*
         * TODO Este codigo iria dentro del loop, para buscar las respuestas De obtener
         * un 301 0 302 hay que buscar como manejar el redirect
         */

        /*
         * Ciclo de lectura del input de la pagina, hay que mejorarlo posiblemente seria
         * buena idea guardar este resultado en un archivo (ej. temp.txt) para tener
         * donde leer/manipular el resultado
         */

        /**
         * Una vez que se tenga el resultado, hay que usar jsoup para crear una lista de
         * tags leer por bloques para poder dar forma al arbol ej. for(getAllh1)
         * getAllahref ... asi, leyendo por tablas/clases, etc aqui mismo, podemos
         * aprovechar a leer la metadata de cada elemento (direccion del salto, nombres
         * de clases, descripciones, etc) eso lo guardamos en una lista, posiblemente un
         * mapa o un hash para poder referenciar de vuelta con la clase
         */

        /**
         * Esta lista, hay que revisar los contenidos, viendo que ,por ejemplo el index,
         * solo deberia salir una vez en el arbol, aun si hay varios saltos al index
         */

        /**
         * Luego se deberia crear un html de respuesta dos columnas, la columna
         * izquierda tendria dos filas
         ***********************************
         * * * * * Arbol * Pagina Web * * * ************************ * Metadata *
         ***********************************
         */
    }
    class IndexItem {
        String name;
        String url;
        ArrayList<String> childrenLinks;
        String metadata;
        IndexItem(String name, String url, ArrayList<String> childrenLinks, String metadata){
            this.name = name;
            this.url = url;
            this.childrenLinks = childrenLinks;
            this.metadata = metadata;
        }
        public String formattedHTML(){
            String result =  "<ul>" +
                    "<li>"+this.name+"</li>"+
                    "<li>"+this.url+"</li>"+
                    "<ul>";
            for(String link:childrenLinks)
                result +="<li>"+link+"</li>";
            result +="</ul>"
                    +"<li>"+this.metadata+"</li>";

            return result;
        }
    }
}