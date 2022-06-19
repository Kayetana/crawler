package test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class PrintPage extends Thread {
    public void run() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("rabbitmq");
            factory.setPassword("rabbitmq");
            factory.setPort(5672);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare("CONTENTS", false, false, true, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String text = new String(delivery.getBody(), "UTF-8");     // get content from CONTENTS...
                System.out.println(text + "\n");                                      // and just print text
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            boolean autoAck = false;
            channel.basicConsume("CONTENTS", autoAck, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            System.out.println("error: "+ e.toString());
        }
    }
}

