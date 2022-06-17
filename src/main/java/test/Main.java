package test;

import org.jsoup.nodes.Document;

public class Main {
    private static String url = "http://inelstal.ru/novosti";

    public static void main(String[] args) {
        try {
            TaskController taskController = new TaskController();
            Document doc = TaskController.getUrl(url);  // скачиваем главную страницу
            TaskController.ParseLinks(doc);             // вытаскиваем с главной страницы ссылки и закидываем их в очередь LINKS

            DownloadPage t1 = new DownloadPage();       // потоки 1, 2 достают ссылки из очереди LINKS, скачивают страницы,
            DownloadPage t2 = new DownloadPage();       // достают текст и кладут его в очередь PAGES
            PrintPage t3 = new PrintPage();
            PrintPage t4 = new PrintPage();             // потоки 3, 4 достают текст из очереди PAGES и выводят его

            t1.start();
            t2.start();
            t3.start();
            t4.start();
            } catch (Exception e) {
            System.out.println("error: "+ e.toString());
        }
    }
}








