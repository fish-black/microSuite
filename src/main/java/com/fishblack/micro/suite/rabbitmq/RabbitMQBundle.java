package com.fishblack.micro.suite.rabbitmq;

import com.fishblack.micro.suite.rabbitmq.consumer.NotificationConsumer;
import com.fishblack.micro.suite.rabbitmq.publisher.NotificationPublisher;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * RabbitMQ bundle for dropwizard.
 */
public class RabbitMQBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private Logger logger = LoggerFactory.getLogger(RabbitMQBundle.class);

    public RabbitMQBundle() {
        //empty
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        //empty
    }

    /**
     * Starts RabbitMQ raw notification consumer and notifications publisher.
     * @param configuration
     * @param environment
     */
    public void run(T configuration, Environment environment) {
        RabbitMQBundleConfiguration config = getRabbitMQBundleConfiguration(configuration);
        startPublisher(config,environment);
        startConsumer(config,environment);
    }

    /**
     * Starts a rabbitmq publisher with given properties.
     * @param config
     */
    private void startPublisher(RabbitMQBundleConfiguration config,Environment env ){
        if((config == null) || (config.getPublishExchange() == null) || config.getPublishExchange().isEmpty()){
            logger.info("Publisher is not configured.");
           return;
        }
        Properties props = new Properties() {
            {
                put(NotificationConstants.HOSTNAME, config.getHost());
                put(NotificationConstants.PORT, config.getPort());
                put(NotificationConstants.VIRTUALHOST, config.getVirtualHost());
                put(NotificationConstants.USER, config.getUsername());
                put(NotificationConstants.PASSWORD, config.getPassword());
                put(NotificationConstants.PUBLISH_EXCHANGE, config.getPublishExchange());
                put(NotificationConstants.THREAD_POOL_SIZE, config.getPublishThreadPoolSize());
                put(NotificationConstants.USE_SSL, String.valueOf(config.isUseSsl()));
        }};
        try {
            NotificationPublisher publisher = NotificationPublisher.getInstance(props);
            env.lifecycle().manage(publisher);
        } catch(NotificationException ex){
            logger.error("Publisher failed to connect to rabbitmq bus with error: {}. Check the connection " +
                    "parameters and credentials.",ex.getMessage(),ex);
        }
    }

    /**
     * Starts a rabbitmq consumer with given properties.
     * @param config
     */
    private void startConsumer(RabbitMQBundleConfiguration config, Environment env ){
        if((config == null) || (config.getConsumeExchange() == null) || config.getConsumeExchange().isEmpty()){
            logger.info("Consumer is not configured.");
            return;
        }
        Properties props = new Properties() {
            {
                put(NotificationConstants.HOSTNAME, config.getHost());
                put(NotificationConstants.PORT, config.getPort());
                put(NotificationConstants.VIRTUALHOST, config.getVirtualHost());
                put(NotificationConstants.USER, config.getUsername());
                put(NotificationConstants.PASSWORD, config.getPassword());
                put(NotificationConstants.CONSUME_EXCHANGE, config.getConsumeExchange());
                put(NotificationConstants.THREAD_POOL_SIZE, config.getConsumerThreadPoolSize());
                put(NotificationConstants.USE_SSL, String.valueOf(config.isUseSsl()));
            }};
        try {
            NotificationConsumer consumer = NotificationConsumer.getInstance(props);
            env.lifecycle().manage(consumer);
        } catch(NotificationException ex){
                logger.error("Consumer failed to connect to rabbitmq bus with error: {}. Check the connection " +
                        "parameters and credentials.",ex.getMessage(),ex);
        }
    }

    /**
     * @param configuration
     * @return
     */
    public RabbitMQBundleConfiguration getRabbitMQBundleConfiguration(T configuration) {
        RabbitMQBundleConfiguration config = new RabbitMQBundleConfiguration();
        return config;
    }
}
