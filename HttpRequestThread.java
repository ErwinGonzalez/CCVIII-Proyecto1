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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
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
    /*TODO esta sera el retorno al web browser, aqui tambien se debe averiguar como se
        puede agregar un panel para la metadata y otro panel para el detalle de los enlaces
        posiblemente una tabla
     */
    private static String returnString = "<html><body>";
    private static String ogRequest = "",ogDomain ="";
    private final static int   PORT_MAX_NUMBER = 100;
    private static boolean[] portList = new boolean[PORT_MAX_NUMBER];
    static int linkNo = 0;
    private static ArrayList<String> urlList = new ArrayList<>();


    /**
     * Este constructor, posiblemente podria recibir la pagina, en lugar de tenerla
     * hardccoded en el metodo run
     */
    public HttpRequestThread() {

    }

    HttpRequestThread(Socket serverConnection) {
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
                ogDomain = ogRequest.substring(0,ogRequest.contains("/")?ogRequest.indexOf("/"):ogRequest.length());

            }
            checkForOpenPorts(ogRequest);
            returnString += "<h1>";
            for(int i = 0;i<PORT_MAX_NUMBER;i++)
                if(portList[i])
                    returnString+=" | "+i+" | ";
            returnString+="</h1>";

            sendGet(ogRequest,0, null);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    /*TODO make this another class, so it can be executed separately
    * find how to synchronize this with the link reading, so the link reading
    * can start only when an open port has been found*/
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
    private void checkForOpenPorts(String url) {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        List<Callable<boolean[]>> taskList = new ArrayList<>();

        for(int i = 0;i<100;i = i+10){
            taskList.add(new PortCheckingThread(removeHttpFromUrl(ogRequest),i,i+9));
        }
        try {
            List<Future<boolean[]>> futures = executorService.invokeAll(taskList);

            int portPosition = 0;
            for (Future<boolean[]> future : futures) {
                //System.out.println(Arrays.toString(future.get()));
                for (int i = 0; i < future.get().length; i++)
                    portList[portPosition++] = future.get()[i];
            }
            System.out.println(Arrays.toString(portList));
        }catch(InterruptedException | ExecutionException ie){
            ie.printStackTrace();
        }
        executorService.shutdown();

    }
    private String removeHttpFromUrl(String ogUrl){
        if (ogUrl.contains("http://"))
            return ogUrl.replace("http://", "");
        if (ogUrl.contains("https://"))
            return  ogUrl.replace("https://", "");
        return ogUrl;
    }

    private boolean isSubDomain(String url){
        if(!ogRequest.isEmpty()){
            String[] url_parts = (removeHttpFromUrl(url)).split("/");
            return url_parts.length > 0? url_parts[0].contains(ogDomain):url.contains(ogDomain);
        }else{
            return false;
        }
    }
    private int getFirstOpenPort(){
        for(int i = 0;i<PORT_MAX_NUMBER;i++){
            if(portList[i])
                return i;
        }
        return -1;
    }
    private void sendGet(String url, int depth, IndexItem receivedItem) throws Exception {
        /* TODO hacer un ciclo, empezar a leer los puertos, leer la primera linea de
         * cada respuesta nos interesa encontrar las respuestas que nos den codigo 200
         * (OK), 301 (Moved Temporarily) o 302(Moved Permantentely)
         */

        /* TODO Se debe de comprobar que los enlaces sean validos, osea no partes de un enlace
        * por ejemplo, nada de /index, solo aceptar example.com/index
        *  el siguiente paso seria de armar los enlaces*/
        /**Port Searching starts here
        * */
        int portNumber = 78;
        int portMaxNumber = 83;
        addUrlToList(url);
        String urlString = removeHttpFromUrl(url);

        //urlString = urlString.contains("/") ? urlString.substring(0, urlString.indexOf("/")):urlString;
        boolean portOpen = false;

        //System.out.println(urlString);
        /*ArrayList<Integer> openPorts = new ArrayList<>();
        if(url.equals(ogRequest)) {
            for (int port = portNumber; port <= portMaxNumber; port++) {
                if (isPortOpen(urlString, port) ) {

                    portNumber = port;
                    openPorts.add(portNumber);
                }
                System.out.print(portNumber);
            }
            returnString += "<h1>" + openPorts + "</h1>";
        }*/
        //System.out.println(openPorts.toString());
        //System.out.println("DEPTH: "+depth);

        /** Port Searching end Here
        * */
        int firstOpenPort = getFirstOpenPort();
        if(firstOpenPort < 0)
            firstOpenPort = 80;
        /**Starts socket and gets page from port obtained before*/
        SocketUtil socketUtil = new SocketUtil(urlString,firstOpenPort );
        socketUtil.sendGet(urlString);

        //addUrlToList(url);

        String str;
        //Arraylist para guardar los elementos leidos de jsoup
        ArrayList<IndexItem> searchLinks = new ArrayList<>();
        ArrayList<String> allLinks = new ArrayList<>();
        // string para guardar el html que devuelve el socket
        String htmlRead = "";
        while ((str = socketUtil.readLineIfNotEmpty()) != null) {
            // TODO esta leyendo un 0/null de mas
            htmlRead += str + "\n";
        }

        // TODO add other stuff a[href], and other links
        Document doc = Jsoup.parse(htmlRead);
        Elements alinks = doc.select("a");
        //TODO button might need to display text or smthng
        Elements lLinks = doc.select("link");
        //Elements btnlinks = doc.getElementsByTag("button");
        //Elements formlinks = doc.getElementsByTag("action");
        //starts html response
        //alinks.addAll(lLinks);
        //System.out.println(urlString +"\n"+ (alinks.size() > 0 )+"\n");

        /**Get links and other stuff, add them to the list*/
        //TODO manage list better, still has to do recursion
        for (Element link : alinks) {
            //System.out.println(link);
            //TODO cambiar a ssl para poder llenar mejor el arbol
            //TODO excluir otros tipos de archivos, como los .ico y los .xml, archivos que no tienen links
            /*TODO crear diferentes tipos de listas 1. una global 2. una local y 3. una que contenga solo enlaces del dominio
                i.e. si se visita la pagina de galileo.edu 1.incluye todos los enlaces, para no revisarlos 2 veces
                2.incluye todos los enlaces que se encuentren en esta pagina
                3. incluye solo los enclaces de galileo.edu, no los de facebook, linkedin, youtube, etc. esta lista es la que se
                usa para crear y recorrer el arbol
             */
            String currLink = link.attr("href");
            if(!currLink.isEmpty())
                allLinks.add(currLink);
            if(!currLink.isEmpty()
                    && addUrlToList(currLink)
                    && isSubDomain(currLink)
                    && !currLink.startsWith("/")
                    && !currLink.startsWith("#")
                    && !currLink.endsWith(".css")
                    && !currLink.endsWith(".ico")
                    && !currLink.endsWith(".jpg")
                    && !currLink.endsWith(".png")
                    && !currLink.contains("twitter")
                    && !currLink.contains("oembed"))
                searchLinks.add(new IndexItem(currLink, htmlRead,new ArrayList<>(),allLinks));
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
            receivedItem = new IndexItem(url,htmlRead,new ArrayList<>(), new ArrayList<>());


        receivedItem.childrenLinks.addAll(searchLinks);
       //System.out.println(receivedItem);
       // System.out.println(receivedItem);
       // System.out.println(list.toString());

        if((depth++)<5) {
            for (IndexItem childLink : searchLinks) {
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
        ArrayList<String> allLinks;
        IndexItem(){}
        IndexItem(String url, String html, ArrayList<IndexItem> childrenLinks, ArrayList<String> allLinks){
            this.url = url;
            this.html = html;
            this.childrenLinks = childrenLinks;
            this.allLinks = allLinks;
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