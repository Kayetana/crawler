package test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskController {
    //private static String server = "http://inelstal.ru";
    public static Document getUrl(String url) {    //download page
        int code = 0;
        Document doc = null;
        CloseableHttpClient client = null;

        client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = null;

        try {
            response = client.execute(request);
            code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                doc = Jsoup.parse(entity.getContent(), "UTF-8", url);
            } else {
                System.out.println("error get url " + url + " code " + code);
            }
            response.close();
        } catch (IOException e) {
            System.out.println("error: "+ e.toString());
        }
        return doc;
    }
    static public void ParseLinks(Document doc) {     //extract links
        Elements news = doc.getElementsByClass("news-itm").select("h3 > a");

        for (Element element: news) {
            String link = element.attr("href");
            AppendLink(link);
        }
    }
    static public void AppendLink(String link) {     //append link to queue
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");
        factory.setPort(5672);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare("LINKS", false, false, true, null);
            channel.basicPublish("", "LINKS", null, link.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("error: "+ e.toString());
        }
    }
}

