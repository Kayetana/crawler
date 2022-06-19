package test;

import org.jsoup.nodes.Document;

public class Main {
    private static String url = "http://inelstal.ru/novosti";

    public static void main(String[] args) {
        try {
            TaskController taskController = new TaskController();
            Document doc = TaskController.getUrl(url);  // download main page
            TaskController.ParseLinks(doc);             // extract links from main page and append them to queue LINKS

            DownloadPage t1 = new DownloadPage();       // threads 1, 2 get links from queue LINKS, download pages
            DownloadPage t2 = new DownloadPage();       // and append them to queue PAGES

            ParsePage t3 = new ParsePage();             // threads 3, 4 get pages from queue PAGES, parse them and
            ParsePage t4 = new ParsePage();             // append content to queue CONTENTS

            PrintPage t5 = new PrintPage();             // threads 5, 6 get content from queue CONTENTS and just print it
            PrintPage t6 = new PrintPage();

            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
            } catch (Exception e) {
            System.out.println("error: "+ e.toString());
        }
    }
}