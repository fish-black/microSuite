package com.fishblack.micro.suite.rabbitmq.consumer;

import com.fishblack.micro.suite.rabbitmq.NotificationConstants;
import com.fishblack.micro.suite.rabbitmq.NotificationException;
import com.fishblack.micro.suite.rabbitmq.RabbitMQ;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 * Consumes messages from RabbitMQ.
 * Usage :
 *  Once the RabbitMQ bundle is created, and if consumer exchange is configured in service.yml
 *  then consumer callback to rabbit is registered. This creates a singleton consumer.
 *  You can also use this class separately from bundle.
 */
public class NotificationConsumer implements Managed {

    private Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);
    private RabbitMQ rabbit = null;
    private String consumeExchange = null;
    private boolean consumeAck = true;
    private Channel channel = null;
    private List<NotificationListener> listenerList = new ArrayList<>();
    private boolean run = true;
    private Long connectRetryInterval = 60000L;
    private final long DEFAULT_CONNECT_RETRY_INTERVAL = 1000*30;
    private static NotificationConsumer instance = null;

    /**
     * Creates a singleton object based on the properties passed.
     * @param props
     * @return
     * @throws NotificationException
     */
    public static NotificationConsumer getInstance(Properties props) throws NotificationException {
        if(instance == null){
            instance = new NotificationConsumer(props);
        }
        return instance;
    }

    /**
     * Returns the single instance. First getInstance(Properties props) needs to be called.
     * @return
     * @throws NotificationException
     */
    public static NotificationConsumer getInstance() throws NotificationException {
        if(instance == null){
            throw new NotificationException("NotificationConsumer object has not being created.");
        }
        return instance;
    }

    private NotificationConsumer(Properties props) throws NotificationException {
        rabbit = new RabbitMQ(props);
        consumeExchange = props.getProperty(NotificationConstants.CONSUME_EXCHANGE);
        consumeAck = Boolean.valueOf(props.getProperty(NotificationConstants.CONSUME_ACKS));
        if(props.contains(NotificationConstants.CONNECT_RETRY_INTERVAL)) {
            connectRetryInterval = Long.valueOf(props.getProperty(NotificationConstants.CONNECT_RETRY_INTERVAL));
        } else {
            connectRetryInterval = DEFAULT_CONNECT_RETRY_INTERVAL;
        }
        checkConnection();
    }

    private void checkConnection(){
        new Thread(() -> {
            while( (!rabbit.isConnected()) && (run)){
                try {
                    Thread.sleep(connectRetryInterval);

                    // retry rabbit connection and consume messages
                    rabbit.createConnection();
                    handleMessage();
                }catch (InterruptedException | NotificationException | IOException ex){
                    run = false;
                    logger.error("Error in retrying rabbit connection", ex);
                }
            }
        }).start();
    }

    /**
     * Managed start
     * @throws IOException
     */
    @Override
    public void start() throws Exception {
        logger.info("Starting notification consumer from exchange {} ", consumeExchange);
        if(!rabbit.isConnected()){
            return;
        }
        handleMessage();
    }

    @Override
    public void stop() throws Exception {
        logger.info("Stopping notification consumer from exchange {} ", consumeExchange);
        run = false;
        if(channel != null && channel.isOpen()) {
            channel.close();
        }
        rabbit.close();
    }

    public void addListener(NotificationListener listener){
        listenerList.add(listener);
    }

    /**
     * Consume message and delegate the request to listener
     * @throws IOException
     */
    private void handleMessage() throws IOException {
        channel = rabbit.getChannelFromCache();
        if(channel == null){
            logger.warn("RabbitMQ is not running ?");
            return;
        }

        channel.exchangeDeclare(consumeExchange,"topic",true);
        String queueName = channel.queueDeclare(
                createNotificationQueueBase(), true, false, false, null).getQueue();
        channel.queueBind(queueName, consumeExchange, "");
        channel.basicConsume(queueName,false, new DefaultConsumer(channel) {
            private String toString(byte []body){
                try {
                    return new String(body, "utf-8");
                }catch (UnsupportedEncodingException e){
                    return new String(body);
                }
            }

            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                super.handleShutdownSignal(consumerTag, sig);
                logger.error("RabbitMQ shutdown", sig);
            }

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                if(!rabbit.getExecutorService().isShutdown()) {
                    rabbit.getExecutorService().submit(() -> {
                        String msg = toString(body);
                        if (channel.isOpen()) {
                            try {
                                logger.debug("Received message: " + msg);
                                listenerList.parallelStream().forEach(listener -> {
                                    try {
                                        listener.handleMessage(msg);
                                    } catch (IOException e) {
                                        logger.error("Failed to persist message.", e);
                                    }
                                });
                                channel.basicAck(envelope.getDeliveryTag(), consumeAck);
                            } catch (IOException e) {
                                logger.error("Failed to persist message.", e);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * This function helps to create base string that has to be appended while defining the queue, currently for notification-svc the base queue string is the only queue that is defined.
     * All additional queues could use the base queue string and append to it with a period '.' sign
    */
    private String createNotificationQueueBase(){
        return (NotificationConstants.NOTIFICATION_SERVICE_APPEND_DEFAULT + "." + NotificationConstants.NOTIFICATION_SERVICE_QUEUE);
    }
}