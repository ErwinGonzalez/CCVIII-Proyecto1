import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

class ServerThread implements Runnable {

    private Socket connectedSocket;
    private final static Logger logger = Logger.getLogger(ServerThread.class.getName());

    private String query = "";
    private int responseCode;
    private int responseSize;
    private String responseType = "";
    private String statusCode = "OK";
    private String headerResponse = "";
    private String responseContent = "";
    private byte[] fileData;
    SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM/yyyy hh:mm:ss z");

    File requestedFile;
    BufferedReader fileBufferedReader;

    private BufferedReader input;

    private DataOutputStream dos;

    public ServerThread(Socket connection) {
        this.connectedSocket = connection;

        try {
            input = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));
            dos = new DataOutputStream(connectedSocket.getOutputStream());
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    @Override
    public void run() {

        try {
            String read = "";
            read = input.readLine();
            String message = read + "\n";

            if (read != null && !read.isEmpty()) {
                readRequestedFile(read);
            } else {
                return;
            }

            /*read = input.readLine();
            while (!read.isEmpty() && read != null) {
                message += read + "\n";
                read = input.readLine();
            }*/

            formatResponse();

            logger.info(message);

            dos.writeBytes(headerResponse);
            dos.writeBytes("\r\n");
            if (query.equals("GET")) {

                dos.write(fileData);
            }
            dos.writeBytes("\r\n");
            dos.flush();
            dos.close();

            connectedSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void formatResponse() {
        headerResponse = "HTTP/1.1 " + responseCode + " " + statusCode + " \n" + "Server : TestServer\n" + "Date: "
                + format.format(new Date(System.currentTimeMillis())) + "\n" + "Content-Type: " + responseType
                + "\n" + "Connection: keep-alive\n" + "Content-Length: " + responseSize + "\r\n";

        if (responseCode == 404)
            headerResponse = "HTTP/1.1 " + responseCode + " " + statusCode + " \n" + "Server : TestServer\n"
                    + "Date: " + format.format(new Date(System.currentTimeMillis())) + "\n"
                    + "Content-Type: text/html\n" + "Content-Lenght: "
                    + (String.valueOf(responseCode).length() + statusCode.length() + 14) + "\n\n" + "<HTML>"
                    + responseCode + " " + statusCode + "</HTML>";
    }

    private void readRequestedFile(String read) throws FileNotFoundException, IOException {
        String[] requestType = read.split(" ");

        if (requestType[0].equals("GET"))
            query = "GET";
        else if (requestType[0].equals("HEAD"))
            query = "HEAD";
        else
            query = "N/A";

        if (requestType[1].substring(requestType[1].length() - 1).equals("/")) {
            requestedFile = new File("." + requestType[1] + "index.html");
            System.out.println(requestedFile.getAbsolutePath());
        } else {
            requestedFile = new File("." + requestType[1]);
        }

        String fileType = requestType[1];
        System.out.println(fileType);

        if (fileType.contains("html"))
            responseType = "text/html";
        else if (fileType.contains("css"))
            responseType = "text/css";
        else if (fileType.contains("js"))
            responseType = "text/js";
        else if (fileType.contains("png"))
            responseType = "image/png";
        else if (fileType.contains("ttf"))
            responseType = "text/ttf";
        else if (fileType.contains("woff"))
            responseType = "text/woff";
        else if (fileType.contains("map"))
            responseType = "gzip";
        if (requestedFile.exists()) {
            responseCode = 200;
            fileBufferedReader = new BufferedReader(new FileReader(requestedFile));
            // TODO read ttf?v=2.0.0 files correctly a.k.a ignore the last part

            fileData = Files.readAllBytes(requestedFile.toPath());
            responseSize = fileData.length;

        } else {
            responseCode = 404;
            responseContent = "";
            statusCode = "Not Found";
        }
    }
}