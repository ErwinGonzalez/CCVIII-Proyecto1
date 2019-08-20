import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class htmlOnClickTesting implements Runnable{

    private Socket connection;
    private BufferedReader input;
    private DataOutputStream dos;

    ArrayList<UrlItem> urlList = new ArrayList<UrlItem>();
    private final String style = "<style>\n.hidden{\ndisplay : none;}\n" +
                                ".shown{\nbackground-color : #9E9E9E;}\n" +
                                ".col1{width : 60%;}\n" +
                                ".col2{width : 40%;}  </style>";
    private final String script = "<script type= \"text/javascript\"\n>"+
            "function testFunction(url){" +
            "var cName = url+\"_html\";\n"+

            "var y = document.getElementsByClassName(\"shown\");"+
            "var i;"+
            "for(i = 0; i<y.length;i++){" +
            "y[i].classList.add(\"hidden\");" +
            "y[i].classList.remove(\"shown\");}" +

            "var x = document.getElementById(cName);\n" +
            "x.classList.remove(\"hidden\");" +
            "x.classList.add(\"shown\");}" +
            "</script>";


    public htmlOnClickTesting(Socket socket){
        this.connection = socket;

        try {
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            dos = new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readAllUrls(UrlItem rootItem){
        String returnString  = "<ul>";
        returnString += "<li>"+"<a href=javascript:testFunction(\""+rootItem.url+"\");>View HTML</a>" +
                "<a href=\"http://www.google.com\" target=\"_blank\">"+rootItem.url+"</a></li>";
        for (UrlItem childItem : rootItem.childUrls){
            //returnString+="<ul><li>"+childItem.url+"</li>";
            returnString += readAllUrls(childItem);
            //returnString+="</ul>";
        }
        returnString += "</ul>";
        return returnString;
    }
    public String readAllHtml(UrlItem rootItem){
        String returnString  = "<div id="+rootItem.url+"_html class=\"hidden\">"+rootItem.url+"<br>"+rootItem.html+"</div>";
        for(UrlItem child: rootItem.childUrls)
           returnString += readAllHtml(child);
        return returnString;
    }

    @Override
    public void run() {
        urlList.add(new UrlItem("child1","content1",new ArrayList<UrlItem>()));
        urlList.add(new UrlItem("child2","content2",new ArrayList<UrlItem>()));
        urlList.add(new UrlItem("child3","content3",new ArrayList<UrlItem>()));
        urlList.add(new UrlItem("child4","content4",new ArrayList<UrlItem>()));
        UrlItem root = new UrlItem("root","root content",urlList);


        String content = "<html>"+
                "<head>"+style+script+"</head>"+
                "<body><table style=\"width:100%\"><tr><th class = \"col1\">Urls</th><th class=\"col2\">html</th></tr></tr><td>"+readAllUrls(root)+"</td>"+"<td>"+readAllHtml(root)+"</td></tr></table>"+

                "</body></html>";
        String htmlResponse = "HTTP/1.1 200 OK\n"+
                               "Content-Type: text/html\n" ;

        System.out.println(htmlResponse);
        htmlResponse+="Content-Lenght: "+content.length()+
                    "\r\n\r\n";
        htmlResponse+=content;
        System.out.println(htmlResponse);
        try {
            dos.writeBytes(htmlResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class UrlItem{
        String url;
        String html;
        ArrayList<UrlItem> childUrls;

        public UrlItem(String s1, String s2, ArrayList<UrlItem> l1){
            this.url = s1;
            this.html = s2;
            this.childUrls = l1;
        }
    }
}
