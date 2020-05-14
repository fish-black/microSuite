package com.fishblack.micro.suite.rabbitmq.publisher;

import com.fishblack.micro.suite.model.Routable;
import com.fishblack.micro.suite.rabbitmq.NotificationConstants;
import com.fishblack.micro.suite.rabbitmq.NotificationException;
import com.fishblack.micro.suite.rabbitmq.RabbitMQ;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Publish's notifications to rabbit exchange.
 * Usage :
 * Once the RabbitMQ bundle is created, and if publish exchange is configured in service.yml
 * then publisher to rabbit is created. This creates a singleton publisher.
 * You can also use this class separately from bundle.
 */
public class NotificationPublisher implements Managed {

    private static Logger logger = LoggerFactory.getLogger(NotificationPublisher.class);

    private RabbitMQ rabbit = null;
    private String publishExchange = null;
    private boolean publishAck = true;
    private static NotificationPublisher instance = null;


    /**
     * Creates a singleton object based on the properties passed.
     *
     * @param props
     * @return
     * @throws NotificationException
     */
    public static NotificationPublisher getInstance(Properties props) throws NotificationException {
        if (instance == null) {
            instance = new NotificationPublisher(props);
        }
        return instance;
    }

    /**
     * Returns the single instance. First getInstance(Properties props) needs to be called.
     *
     * @return
     * @throws NotificationException
     */
    public static NotificationPublisher getInstance() throws NotificationException {
        if (instance == null) {
            throw new NotificationException("Failed to initialize rabbitmq. Check the configuration.");
        }
        return instance;
    }

    private NotificationPublisher(Properties props) throws NotificationException {
        rabbit = new RabbitMQ(props);
        publishExchange = props.getProperty(NotificationConstants.PUBLISH_EXCHANGE);
    }

    @Override
    public void start() throws Exception {
        logger.info("Starting notification publisher to exchange {} ", publishExchange);
    }

    /**
     * Sends a message. This is non-blocking/async send.
     * To make it blocking/sync call, use Future.get()
     *
     * @param routableMsg
     * @return
     * @throws IOException
     */
    public Future<String> send(Routable routableMsg) throws IOException {
        String jsonMsg = routableMsg.getJsonMessage();
        String routingKey = routableMsg.getRoutingKey();
        return send(jsonMsg, routingKey);
    }

    /**
     * Send a message with given routing key.This is non-blocking/async send.
     * To make it blocking/sync call, use Future.get()
     *
     * delivery mode set to 2 (persistent) to ensure messages survive
     * rabbitmq restarts
     *
     * @param jsonMsg
     * @param routingKey can be empty, not null.
     * @return a Future object if rabbitmq is available , null if failed to connect to rabbit.
     * @throws IOException
     */
    public Future<String> send(String jsonMsg, String routingKey) throws IOException {
        if (!rabbit.isConnected()) {
            logger.debug("Not connected to rabbitmq. Reconnecting with {}", rabbit.properties());
            try {
                rabbit.createConnection();
            } catch (NotificationException ex) {
                throw new IOException("Not connected to rabbitmq." + ex.getMessage());
            }
        }
        Channel channel = rabbit.getChannelFromCache();
        if(channel == null){
            logger.warn("Failed to connect to rabbitmq, unable to publish message: {}",jsonMsg );
            return null;
        }

        channel.exchangeDeclare(publishExchange, "topic", true);
        return rabbit.getExecutorService().submit(
                () -> {
                    String status = "Sent";
                    if (channel.isOpen()) {
                        try {
                            logger.debug("Going to publish message " + jsonMsg + " to exchange " + publishExchange + " with routing key " + routingKey);
                            channel.basicPublish(publishExchange, (routingKey == null) ? "" : routingKey,
                                    new AMQP.BasicProperties.Builder()
                                    .deliveryMode(2)
                                    .build(), jsonMsg.getBytes());
                        } catch (IOException e) {
                            logger.error("Failed to publish message.", e);
                            status = e.getMessage();
                        }
                    } else {
                        logger.error("Channel is closed. Failed to publish message.");
                        status = "Failed";
                    }
                    return status;
                });
    }

    @Override
    public void stop() throws Exception {
        logger.info("Stopping notification publisher to exchange {} ", publishExchange);
        rabbit.close();
    }

    public boolean isConnected() {
        return rabbit.isConnected();
    }
}