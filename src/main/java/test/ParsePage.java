package test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.nio.charset.StandardCharsets;

public class ParsePage extends Thread {
    public static void AppendContent(String page) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");
        factory.setPort(5672);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare("CONTENTS", false, false, true, null);
            channel.basicPublish("", "CONTENTS", null, page.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("error: " + e.toString());
        }
    }
    public String GetContent(String html) {
        String text = null;
        Document ndoc = Jsoup.parse(html);
        if (ndoc != null) {
            Elements newsDoc = ndoc.getElementsByClass("b-page__content");
            text = newsDoc.text();
        }
        return text;
    }

    public void run() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("rabbitmq");
            factory.setPassword("rabbitmq");
            factory.setPort(5672);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("PAGES", false, false, true, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String page = new String(delivery.getBody(), "UTF-8");  // get page from queue PAGES
                try {
                    String content = null;
                    content = GetContent(page);
                    AppendContent(content);               // append text to queue CONTENTS
                } catch (Exception e) {
                    System.out.println("error while parsing page " + e.toString());
                } finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            boolean autoAck = false;
            channel.basicConsume("PAGES", autoAck, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            System.out.println("error: " + e.toString());
        }
    }
}
