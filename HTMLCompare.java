import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

import java.util.*;

public class HTMLCompare {
    public static ArrayList<PageHTML> pages = new ArrayList<>();

    public HTMLCompare(String baseHTML){

    }
    public static void main(String[] args){
        DummyHTML dh = new DummyHTML();
        String dummyHtml1 =  dh.html1;
        String dummyHtml2 = dh.html2;
        pages.add(new PageHTML(dummyHtml1));
        PageHTML html2 = new PageHTML(dummyHtml2);
        for(PageHTML pageHTML : pages){
            System.out.println(pageHTML.isEqual(html2));
        }
    }

    static class PageHTML {
        public String httpStatusResponse;
        public Map<String, String> httpResponse;
        public String htmlResponse;
        public ArrayList<String> links;
        public ArrayList<String> scripts;
        public String metaDesc;
        public String metaKey;

        public PageHTML(String html){
            httpResponse = new HashMap<>();
            links = new ArrayList<>();
            scripts = new ArrayList<>();
            Document page = Jsoup.parse(html);
            String[] lines = html.split("\\n");
            httpStatusResponse = lines[0];
            String test = "";
            int i = 1;
            while(!(test = lines[i++]).equals("")){
                String[] headerParams = test.split(":");
                httpResponse.put(headerParams[0],headerParams[1]);
                //System.out.println(test);
            }
            htmlResponse  = page.toString();
            Elements elements = page.select("a[href],link:not([rel=shortlink] " + ", [rel=alternate]" + ", [rel=amphtml]"
                    + ", [rel=attachment]" + ", [rel=canonical]" + ", [rel=stylesheet])");
            for(Element element : elements)
                links.add(element.toString());
            elements = page.select("script[src]");
            for(Element element : elements)
                links.add(element.toString());
            elements = page.select("meta[name=description]");
            if(elements.size()>1)
                metaDesc = elements.first().toString();
            elements = page.select("meta[name=keywords]");
            if(elements.size()>1)
                metaKey = elements.first().toString();
        }
        public boolean isEqual(PageHTML page){

            if(this.links.size() == page.links.size() && this.scripts.size() == page.scripts.size()) {
                Collections.sort(this.links);
                Collections.sort(page.links);
                Collections.sort(this.scripts);
                Collections.sort(page.scripts);
                if(this.links.equals(page.links) && this.scripts.equals(page.scripts)){
                    if(this.htmlResponse.equals(page.htmlResponse))
                        return true;
                    else {
                        StringMetric cosMetric = StringMetrics.cosineSimilarity();
                        StringMetric levMetric = StringMetrics.levenshtein();
                        if (cosMetric.compare(this.htmlResponse, page.htmlResponse) > 0.9
                            //&& levMetric.compare(this.htmlResponse, page.htmlResponse)>0.9
                        )
                            return true;
                    }
                }
            }
            return false;
        }
    }
}
