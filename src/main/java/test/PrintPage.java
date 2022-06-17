package test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class PrintPage extends Thread
{
    public void run()
    {
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
                String text = new String(delivery.getBody(), "UTF-8");  // get text from PAGES
                System.out.println(text + "\n");                                    // print text
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            boolean autoAck = false;
            channel.basicConsume("PAGES", autoAck, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            System.out.println("error: "+ e.toString());
        }
    }
}

