import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class IndexItem {
    String url;
    String html;
    ArrayList<IndexItem> childrenLinks;
    static ArrayList<String> uniqueUrls = new ArrayList<>();
    IndexItem() {
    }

    IndexItem(String url, String html, ArrayList<IndexItem> childrenLinks) {
        this.url = url;
        this.html = html;
        this.childrenLinks = childrenLinks;
    }

    public String formatUrlList() {
        String result = "<li>" + "<a href=javascript:testFunction(\"" + this.url + "\");>View HTML</a>"
                + "<a href=javascript:hideChildrenList(\"" + this.url + "\");>[toggle children]</a>"
                + "<a href=\"http://www.google.com\" target=\"_blank\">" + this.url + "</a></li>";
        /*
         * for(IndexItem link:childrenLinks) { result += "<li>" +
         * "<a href=http://localhost:2407/" + link.url + "> a[href]" + link.url + "</a>"
         * + "</li>"; result += formatList(link.childrenLinks); }
         */
        result += "<ul id=" + this.url + "_list>";

        for(IndexItem item : this.childrenLinks) {
            result +=  item.formatUrlList();
        }
        result += "</ul>";

        return result;
    }

    public String formatList(ArrayList<IndexItem> childList) {
        String res = "";

        for (IndexItem childItem : childList) {
            // System.out.println(childItem.childrenLinks+"\n\n");
            res += "<li>" + "<a href=javascript:testFunction(\"" + childItem.url + "\");>[META]</a>";
            if (!childItem.childrenLinks.isEmpty())
                res += "<a href=javascript:hideChildrenList(\"" + childItem.url + "\");>[toggle children]</a>";
            res += "<a href=\"http://www.google.com\" target=\"_blank\">" + childItem.url + "</a>";
            if (!childItem.childrenLinks.isEmpty()) {

                res += "<ul id=" + childItem.url + "_list>" + formatList(childItem.childrenLinks) + "</ul></li>";
            }
        }
        // System.out.println(res);
        return res;
    }
    public String readAllHtml() {
        String returnString = "<div id=" + this.url + "_html class=\"hidden\">" ;
        //+ rootItem.html.replace("<", "&lt;").replace(">", "&gt;") + "</div>";

        Document doc = Jsoup.parse(this.html);
        returnString+= "<h3>"+doc.title()+"</h3><br>"+ this.url + "<br>"+"<h4>Description:</h4> ";
        Elements elements = doc.select("meta[name=description]");
        for (Element element : elements){
            returnString+="<li>"+element.toString().replace("<", "&lt;").replace(">", "&gt;")+"</li>";
        }
        returnString+="<br><h4>Keywords</h4>";
        elements = doc.select("meta[name=keywords]");
        for (Element element : elements){
            returnString+="<li>"+element.toString().replace("<", "&lt;").replace(">", "&gt;")+"</li>";
        }
        elements = doc.select("a,link,onClick");
        ArrayList<String> links = new ArrayList<>();
        for(Element link : elements){
            String url = link.attr("href");
            if(uniqueUrls.indexOf(url)<0){
                uniqueUrls.add(url);
                links.add(url);
            }
        }
        returnString+="<br><h4>Links</h4>";
        for (String s : links){
            returnString+="<li>"+s+"</li>";
        }
        returnString +="</div>";
        for(IndexItem item : this.childrenLinks){
            returnString += item.readAllHtml();
        }
        return returnString;
    }
    public String readChildrenHTML(ArrayList<IndexItem> list){
        String returnString = "<div id=" + this.url + "_html class=\"hidden\">" + this.html.replace("<", "&lt;").replace(">", "&gt;") + "</div>";
        returnString += readChildrenHTML(this.childrenLinks);
        return  returnString;
    }

    public String toString() {
        return "url: " + this.url + " links: " + this.childrenLinks + "\n";
    }

}
