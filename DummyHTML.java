public class DummyHTML {
     String html1 = "HTTP/1.x 200 OK\n" +
                "Transfer-Encoding: chunked\n" +
                "Date: Sat, 28 Nov 2009 04:36:25 GMT\n" +
                "Server: LiteSpeed\n" +
                "Connection: close\n" +
                "X-Powered-By: W3 Total Cache/0.8\n" +
                "Pragma: public\n" +
                "Expires: Sat, 28 Nov 2009 05:36:25 GMT\n" +
                "Etag: \"pub1259380237;gz\"\n" +
                "Cache-Control: max-age=3600, public\n" +
                "Content-Type: text/html; charset=UTF-8\n" +
                "Last-Modified: Sat, 28 Nov 2009 03:50:37 GMT\n" +
                "X-Pingback: https://net.tutsplus.com/xmlrpc.php\n" +
                "Content-Encoding: gzip\n" +
                "Vary: Accept-Encoding, Cookie, User-Agent\n" +
                "\n" +

             "<html data-app-version=\"509bfc481d7d709eb393d4dd3f63a1dc02c1424f-production\" class=\" js no-touch history\" style=\"\"><head><link rel=\"stylesheet\" media=\"all\" href=\"https://static.tutsplus.com/assets/application-36f79654507cd4307bd59c256e7ad7e4.css\"><link crossorigin=\"true\" href=\"https://fonts.gstatic.com/\" rel=\"preconnect\"><link href=\"https://fonts.googleapis.com/css?family=Roboto:400,500,700\" rel=\"stylesheet\" type=\"text/css\"><meta charset=\"utf-8\">\n" +
             "<link rel=\"stylesheet\" media=\"all\" href=\"https://static.tutsplus.com/assets/application-36f79654507cd4307bd59c256e7ad7e4.css\"><link crossorigin=\"true\" href=\"https://fonts.gstatic.com/\" rel=\"preconnect\"><link href=\"https://fonts.googleapis.com/css?family=Roboto:400,500,700\" rel=\"stylesheet\" type=\"text/css\"><meta charset=\"utf-8\">\n" +
             "<script type=\"text/javascript\" src=\"https://bam.nr-data.net/1/27851e2035?a=3094824&amp;v=1130.54e767a&amp;to=JQpZTURXXlRcRBYWRBIKRVBXVEEXSl5WFQ%3D%3D&amp;rst=2661&amp;ref=https://code.tutsplus.com/tutorials/http-headers-for-dummies--net-8039&amp;ap=157&amp;be=176&amp;fe=2636&amp;dc=1021&amp;perf=%7B%22timing%22:%7B%22of%22:1566955857675,%22n%22:0,%22f%22:11,%22dn%22:11,%22dne%22:11,%22c%22:11,%22ce%22:11,%22rq%22:14,%22rp%22:15,%22rpe%22:57,%22dl%22:59,%22di%22:780,%22ds%22:780,%22de%22:1913,%22dc%22:2635,%22l%22:2635,%22le%22:2640%7D,%22navigation%22:%7B%22ty%22:2%7D%7D&amp;fp=395&amp;fcp=395&amp;jsonp=NREUM.setToken\"></script><script src=\"https://js-agent.newrelic.com/nr-1130.min.js\"></script><script type=\"text/javascript\" async=\"\" src=\"https://www.google-analytics.com/plugins/ua/linkid.js\"></script><script type=\"text/javascript\" async=\"\" src=\"https://www.google-analytics.com/plugins/ua/ec.js\"></script><script type=\"text/javascript\">window.NREUM||(NREUM={});NREUM.info={\"beacon\":\"bam.nr-data.net\",\"errorBeacon\":\"bam.nr-data.net\",\"licenseKey\":\"27851e2035\",\"applicationID\":\"3094824\",\"transactionName\":\"JQpZTURXXlRcRBYWRBIKRVBXVEEXSl5WFQ==\",\"queueTime\":0,\"applicationTime\":157,\"agent\":\"\"}</script>\n" +
             "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
             "<title>Top 20+ MySQL Best Practices - Nettuts+</title></head><body class=\"page-body topic-code\" data-ga-account=\"UA-11792865-37\" data-ga-autolink-domains=\"activeden.net,audiojungle.net,themeforest.net,videohive.net,graphicriver.net,3docean.net,codecanyon.net,photodune.net,market.envato.com,market.styleguide.envato.com,elements.envato.com,build.envatohostedservices.com,author.envato.com,tutsplus.com,envato.com,business.tutsplus.com,design.tutsplus.com,code.tutsplus.com,webdesign.tutsplus.com,photography.tutsplus.com,music.tutsplus.com,cgi.tutsplus.com,gamedevelopment.tutsplus.com,computers.tutsplus.com,crafts.tutsplus.com\" data-ga-domain=\"tutsplus.com\" data-wt-dcsid=\"dcs222cpd4g6lg7wb3cum8l5g_6w6m\"><div class=\"announcement-bar view announcement-bar--open view--loaded\" data-announcement-id=\"header_promo_aug19\" data-background-image=\"https://cms-assets.tutsplus.com/uploads/users/15/topics/7/announcement_background_image/new-elements-bg.png\" data-fallback-background-color=\"#273ab2\" data-link-color=\"#2d2d2d\" data-text-color=\"#ffffff\" style=\"background-image: url(&quot;https://cms-assets.tutsplus.com/uploads/users/15/topics/7/announcement_background_image/new-elements-bg.png&quot;); background-size: inherit; background-color: rgb(39, 58, 178); color: rgb(255, 255, 255);\"><div class=\"announcement-bar__content\"><div class=\"announcement-bar__primary\"><div class=\"announcement-bar__title\"><div align=\"center\">\n" +
             "    <span class=\"announcement-bar__title-desktop\">\n" +
             "        <strong>Unlimited </strong>\n" +
             "         Plugins, WordPress themes, videos &amp; courses!\n" +
             "    </span>\n" +
             "    <span class=\"announcement-bar__title-mobile\">\n" +
             "        <strong>Unlimited asset downloads!</strong>\n" +
             "    </span>\n</body></html>";


     String html2 = "HTTP/1.1 200 OK\n" +
            "Server: nginx/1.6.2\n" +
            "Date: Wed, 28 Aug 2019 01:26:02 GMT\n" +
            "Content-Type: text/html; charset=UTF-8\n" +
            "Content-Length: 15190\n" +
            "Connection: keep-alive\n" +
            "Link: <http://www.galileo.edu/wp-json/>; rel=\"https://api.w.org/\"\n" +
            "Link: <http://www.galileo.edu/>; rel=shortlink\n" +
            "Vary: Accept-Encoding\n" +
            "Content-Encoding: gzip\n" +
            "Cache-Control: max-age=0\n" +
            "Expires: Wed, 28 Aug 2019 01:26:02 GMT\n" +
            "Access-Control-Allow-Origin: *\n" +
            "\n" +
             "<html lang=\"es\" class=\"no-js\"><head>\n" +
             "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\">\n" +
             "    <meta charset=\"UTF-8\">\n" +
             "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
             "    <link rel=\"shortcut icon\" type=\"image/png\" href=\"http://www.galileo.edu/wp-content/themes/galileo-theme/img/favicon.ico\">\n" +
             "    <link rel=\"pingback\" href=\"http://www.galileo.edu/xmlrpc.php\">\n" +
             "\t\t<title>Universidad Galileo</title>\n" +
             "\n" +
             "        <!--[if IE 8]>\n" +
             "            .btn-folleto {\n" +
             "                height: 85px !important\n" +
             "                ;\n" +
             "            }\n" +
             "        <![endif]-->\n" +
             "    \n" +
             "<!-- All in One SEO Pack 2.11 by Michael Torbert of Semper Fi Web Design[409,435] -->\n" +
             "<meta name=\"description\" content=\"Portal de la universidad. Información académica e institucional. Formamos profesionales con excelencia académica. Somos una entidad educativa que promueve el\">\n" +
             "\n" +
             "<meta name=\"keywords\" content=\"Universidad, galileo, galilei, educación, tecnología, ciencia, revolución, académica, académico, institucional, institución, universidades, portal, público, establecimiento, educativo,\n" +
             "enseñanza, superior, centros, estudio, estudiar, investigación, capaces, formamos, formación, trasformación, sistemas, informática, preparación, profesionales, carreras, facultades,\n" +
             "institutos, programas, departamentos, pensamiento, humano, excelencia, preparación, laboratorios, personal, autoridades, innovación, inteligentes, Eduardo, Suger, librería,\n" +
             "servicios, información, impulsamos, evolución, medialab, noticias, multimedia, eventos, mentes, brillantes, valores, servicios, justicia, astronomía, investigación, desarrollo, admisiones, exámenes, ubicación, inscripción, ingreso, reingreso, estudiar, licenciatura, maestría, postgrado, diplomado, técnico\">\n" +
             "\n" +
             "<link rel=\"canonical\" href=\"http://www.galileo.edu/\"></head><body data-spy=\"scroll\" data-target=\"#section-spy\" data-offset=\"0\" class=\"header-sticky\">\n" +
             "\t<h1 class=\"ug-seo-google\" style=\"top: -9999px !important;left: -9999px !important;position: absolute !important;\">Home</h1>\n" +
             "        <div class=\"menu-services-ug\">\n" +
             "\t\t<div class=\"menu-hover\">\n" +
             "\t\t\t<div class=\"btn-menu\">\n" +
             "\t\t\t\t<span></span>\n" +
             "\t\t\t</div>\n" +
             "\t\t</div>\n" +
             "      <nav class=\"navbar menu-top\"><ul id=\"secundary_menu\" class=\"menu\"><li class=\"menu-ges menu-item menu-item-type-custom menu-item-object-custom menu-item-65443 nav-item ges\"><a href=\"http://www.galileo.edu/register/\" class=\"nav-link\">GES</a></li>\n" +
             "<li class=\"menu-item menu-item-type-custom menu-item-object-custom menu-item-65444 nav-item correo\"><a href=\"http://correo.galileo.edu\" class=\"nav-link\">CORREO</a></li>\n" +
             "<li class=\"menu-item menu-item-type-custom menu-item-object-custom menu-item-65445 nav-item pagar-mi-u\"><a href=\"/pagos/no-registrados/\" class=\"nav-link\">PAGAR MI U</a></li>\n\n</body></html>"
            ;
    public DummyHTML(){}
}
