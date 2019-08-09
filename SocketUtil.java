import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

class SocketUtil {
    Socket socket;
    BufferedReader socketInputStream;
    PrintWriter socketOutputStream;
    String lastReadMessage;
    ArrayList<String> fullReadMessage;
    String headerSeparator = "\r\n\r\n";
    boolean headerComplete = false;
    String domain;
    String page;
    int port;

    public SocketUtil(String url, int port) throws IOException {
        this.port = port;
        resetSocket(url, port);
    }

    public void sendGet(String url) throws IOException {
        this.domain = getDomain(url);
        this.page = getSite(url);
        System.out.println("domain "+domain+"site "+page);
        String message = "GET " + page + " HTTP/1.1\r\n" + "Host: " + domain + "\r\n" + "Accept: */*\r\n"
                + "Connection: keep-alive\r\n" + "User-Agent: Mozilla/5.0\r\n";
        socketOutputStream.println(message);
        socketOutputStream.println(headerSeparator);
        socketOutputStream.flush();
        int responseCode = getResponseCode();
        if (responseCode == 301 || responseCode == 302)
            getForwardAddress();

    }

    public String readOneLine() throws IOException {
        if ((lastReadMessage = socketInputStream.readLine()) != null) {

            fullReadMessage.add(lastReadMessage);

            return lastReadMessage;
        } else {
            return null;
        }

    }

    public int getResponseCode() throws IOException {
        socketInputStream.mark(1000);
        readOneLine();
        socketInputStream.reset();
        String[] requestLine = lastReadMessage.split(" ");
        // System.out.println(lastReadMessage);
        return Integer.parseInt(requestLine[1]);
    }

    public void getForwardAddress() throws IOException {
        boolean finished = false;
        String[] requestLine = lastReadMessage.split(" ");
        while (!finished) {
            if (requestLine[0].equals("Location:"))
                finished = true;
            else {
                readOneLine();
                requestLine = lastReadMessage.split(" ");
            }
        }
        // String[] redirectPage = requestLine[1].split("/");
        /*
         * System.out.println(requestLine[1]); for(String part: redirectPage)
         * System.out.println(part);
         */

        /*
         * if(redirectPage.length == 4) this.page = redirectPage[3]; this.domain =
         * redirectPage[2];
         */

        // TODO get the correct path, splitting on "/" seems not appropiate
        resetSocket(getDomain(requestLine[1]), port);
     
        // System.out.println(requestLine[1]);
        // System.out.println(site+"\n"+getDomain(site)+"\n"+getSite(site));
        sendGet(requestLine[1]);
      
        // TODO make the new GET request
    }

    public String removeHttp(String url) {
        String ret = url;
        if (url.contains("http://"))
            ret = url.replace("http://", "");
        if (url.contains("https://"))
            ret = url.replace("https://", "");
        return ret;
    }

    public String getDomain(String url) {
        String site = removeHttp(url);
        return site.contains("/") ? site.substring(0, site.indexOf("/")):site;
    }

    public String getSite(String url) {
        String site = removeHttp(url);
        return site.contains("/") ? site.substring(site.indexOf("/"),site.length()) : "/";
        
    }

    public void resetSocket(String url, int port) throws IOException {
        try {
            socket = new Socket(getDomain(url), port);
            socketInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        fullReadMessage = new ArrayList<>();
    }

    /*
     * TODO readAll doesn't work propertly, bug, returns the sent message, not the
     * inputStream Do not use
     */
    public ArrayList<String> readAll() throws IOException {
        while ((lastReadMessage = readOneLine()) != null)
            fullReadMessage.add(lastReadMessage);

        return fullReadMessage;
    }

    public void CloseSocket() throws IOException {
        socket.close();
        socketInputStream.close();
        socketOutputStream.close();
    }

}