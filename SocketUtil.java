import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

class SocketUtil{
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
    public SocketUtil(String url, int port){
        this.port = port;
        try{
            //TODO check if the URL is trying to access a page other than the index, split host and page
            socket = new Socket(url,port);
            socketInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))); 
        }catch(UnknownHostException uhe){
            uhe.printStackTrace();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        fullReadMessage = new ArrayList<>();
    }

    public void sendGet(String domain, String page)throws IOException {
        this.domain = domain;
        this.page = page;
        String message = "GET "+page+" HTTP/1.0\r\n"+
                         "Host: "+domain+"\r\n"+
                         "Accept: */*\r\n"+
                         "Connection: keep-alive\r\n"+
                         "User-Agent: Mozilla/5.0\r\n";
        socketOutputStream.println(message);
        socketOutputStream.println(headerSeparator);     
        socketOutputStream.flush();
        int responseCode = getResponseCode();
        if(responseCode == 301 || responseCode == 302)
            getForwardAddress();

            

            for(String line: fullReadMessage)        {
                System.out.println(line);
            }
    }

    public String readOneLine() throws IOException{
        if((lastReadMessage= socketInputStream.readLine())!=null){
            
                fullReadMessage.add(lastReadMessage);
            
            return lastReadMessage;
        }
        return null;
    }

    public int getResponseCode() throws IOException {
        readOneLine();
        String[] requestLine = lastReadMessage.split(" ");
        System.out.println(lastReadMessage);
        return Integer.parseInt(requestLine[1]);
    }

    public void getForwardAddress() throws IOException {
        boolean finished = false;
        String[] requestLine= lastReadMessage.split(" ");
        while(!finished){
            if(requestLine[0].equals("Location:"))
                finished = true;
            else
                readOneLine();
            requestLine = lastReadMessage.split(" ");
        }
        String[] redirectPage = requestLine[1].split("/");
        System.out.println(requestLine[1]);
        for(String part: redirectPage)
            System.out.println(part);

        /*if(redirectPage.length == 4)
            this.page = redirectPage[3];
        this.domain = redirectPage[2];*/

        try{
            socket = new Socket(redirectPage[2],port);
            socketInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))); 
        }catch(UnknownHostException uhe){
            uhe.printStackTrace();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        fullReadMessage = new ArrayList<>();
        //TODO get the correct path, splitting on "/" seems not appropiate
        sendGet(redirectPage[2],"/"+redirectPage[3]+"/");
        
        //TODO make the new GET request
    }
    
    /*TODO readAll doesn't work propertly, bug, returns the sent message, not the inputStream
     *Do not use
     */
    public ArrayList<String> readAll() throws IOException{
        if(lastReadMessage != null)
            fullReadMessage.add(lastReadMessage);
        while((lastReadMessage = readOneLine()) != null)
            fullReadMessage.add(lastReadMessage);
        return fullReadMessage;
    }
    public void CloseSocket() throws IOException{
        socket.close();
        socketInputStream.close();
        socketOutputStream.close();
    }


}