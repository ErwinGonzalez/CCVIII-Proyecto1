import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.management.InstanceNotFoundException;
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
    static String returnString = "<html><body>";
    static String ogRequest = "";
    static int linkNo = 0;
    private static ArrayList<String> urlList = new ArrayList<>();

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
                ogRequest = requestType[1].substring(1);
                sendGet(ogRequest,0, null);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean isPortOpen(String url,int port){
        Socket s = new Socket();
        try{
            //timeout para no esperar al timeout standar
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
    private void sendGet(String url, int depth, IndexItem receivedItem) throws Exception {
        /*
         * TODO hacer un ciclo, empezar a leer los puertos, leer la primera linea de
         * cada respuesta nos interesa encontrar las respuestas que nos den codigo 200
         * (OK), 301 (Moved Temporarily) o 302(Moved Permantentely)
         */
        /**Port Searching starts here
        * */
        int portNumber = 78;
        int portMaxNumber = 83;

        String urlString = url;
        if (urlString.contains("http://"))
            urlString = urlString.replace("http://", "");
        if (url.contains("https://"))
            urlString = urlString.replace("https://", "");
        //urlString = urlString.contains("/") ? urlString.substring(0, urlString.indexOf("/")):urlString;
        boolean portOpen = false;

        //System.out.println(urlString);
        //ArrayList<Integer> openPorts = new ArrayList<>();
        /*for(int port = portNumber;port<=portMaxNumber;port++) {
            if(isPortOpen(urlString,port) && !portOpen) {
                portOpen = true;
                portNumber = port;
                openPorts.add(portNumber);
            }
           // System.out.println("isPortOpen: "+portOpen+" portNumber: "+port);
        }*/
        //System.out.println(openPorts.toString());
        //System.out.println("DEPTH: "+depth);

        /** Port Searching end Here
        * */

        /**Starts socket and gets page from port obtained before*/
        SocketUtil socketUtil = new SocketUtil(urlString, 80);
        socketUtil.sendGet(urlString);

        //addUrlToList(url);

        String str;
        //Arraylist para guardar los elementos leidos de jsoup
        ArrayList<IndexItem> list = new ArrayList<>();
        // string para guardar el html que devuelve el socket
        String htmlRead = "";
        while ((str = socketUtil.readLineIfNotEmpty()) != null) {
            // TODO esta leyendo un 0/null de mas
            htmlRead += str + "\n";
        }

        // TODO add other stuff a[href], and other links
        Document doc = Jsoup.parse(htmlRead);
        Elements alinks = doc.select("a[href]");
        //TODO button might need to display text or smthng
        Elements lLinks = doc.select("link[href]");
        //Elements btnlinks = doc.getElementsByTag("button");
        //Elements formlinks = doc.getElementsByTag("action");
        //starts html response
        alinks.addAll(lLinks);
        //System.out.println(urlString +"\n"+ (alinks.size() > 0 )+"\n");

        /**Get links and other stuff, add them to the list*/
        //TODO manage list better, still has to do recursion
        for (Element link : alinks) {
            //System.out.println(link);
            //TODO cambiar a ssl para poder llenar mejor el arbol
            //TODO excluir otros tipos de archivos, como los .ico y los .xml, archivos que no tienen links
            if(addUrlToList(link.attr("href"))
                    && !link.attr("href").startsWith("/")
                    && !link.attr("href").startsWith("#")
                    && !link.attr("href").endsWith(".css")
                    && !link.attr("href").contains("twitter")
                    && !link.attr("href").contains("oembed")
                    && !link.attr("href").isEmpty())
                list.add(new IndexItem(link.attr("href"), htmlRead,new ArrayList<>()));
            // System.out.println(link);
            //returnString += "<a href="+link.attr("href")+">"+link.attr("href")+"</a><br>\n";
        }
        socketUtil.closeSocket();
        //System.out.println(list.toString());
       /* for (Element link : btnlinks)
            list.add("<a href=http://localhost:2407/"+link.attr("onClick")+"> btn[onClick]"+link.attr("onClick")+"</a>");
        for (Element link : formlinks)
            list.add("<a href=http://localhost:2407/"+link.attr("action")+"> form[action]"+link.attr("action")+"</a>");
        //creates data structure*/

       //TODO add loop here

        if(receivedItem == null)
            receivedItem = new IndexItem(url,htmlRead,new ArrayList<>());


        receivedItem.childrenLinks.addAll(list);
       //System.out.println(receivedItem);
       // System.out.println(receivedItem);
       // System.out.println(list.toString());

        if((depth++)<3) {
            for (IndexItem childLink : list) {
                try {
                    System.out.println(childLink.url);
                    sendGet(childLink.url, depth,childLink);
                } catch (Exception e) {
                    e.printStackTrace();
                }
               // System.out.println(childLink);
            }
        }

        if(url.equals(ogRequest))
            depth = 0;

        //Closes html response code
        if(depth == 0) {
            returnString += receivedItem.formattedHTML() + "</body></html>";
            //System.out.println(returnString);
            String headerResponse = "HTTP/1.1 200 OK" + "Server : TestServer\n" + "Date: "
                    + format.format(new Date(System.currentTimeMillis())) + "\n" + "Content-Type: text/html" + "\n"
                    + "Connection: keep-alive\n" + "Content-Length: " + returnString.length() + "\r\n\r\n";

            output.writeBytes(headerResponse);
            output.writeBytes(returnString);
            //System.out.println(urlList);

        }
        /**
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
        return;
    }
    private boolean addUrlToList(String url){
        if(!urlList.contains(url)) {
            urlList.add(url);
            return true;
        }
        return false;
    }
    class IndexItem {
        String url;
        String html;
        ArrayList<IndexItem> childrenLinks;
        IndexItem(){}
        IndexItem(String url, String html, ArrayList<IndexItem> childrenLinks){
            this.url = url;
            this.html = html;
            this.childrenLinks = childrenLinks;
        }
        public String formattedHTML(){
            String result =  "<ul>"+
                    "<li>"+this.url+"</li>";
            /*for(IndexItem link:childrenLinks) {
                result += "<li>" + "<a href=http://localhost:2407/" + link.url + "> a[href]" + link.url + "</a>" + "</li>";
                result += formatList(link.childrenLinks);
            }*/
            if(!this.childrenLinks.isEmpty())
                result+=formatList(childrenLinks);
            result +="</ul>";


            return result;
        }
        public String formatList(ArrayList<IndexItem> childList ){
            String res = "<ul>";

                for(IndexItem childItem : childList) {
                   // System.out.println(childItem.childrenLinks+"\n\n");
                    res+="<li>" + "<a href=http://localhost:2407/" + childItem.url + "> a[href]" + childItem.url + "</a>" + "</li>";
                   // if(childItem.childrenLinks != null)
                    res += formatList(childItem.childrenLinks);
                }
            //System.out.println(res);
            res +="</ul>";
            return res;
        }
        public String toString(){
            return "url: "+this.url +" links: "+this.childrenLinks+"\n";
        }
    }
}