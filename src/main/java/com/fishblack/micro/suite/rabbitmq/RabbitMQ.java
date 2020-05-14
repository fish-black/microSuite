package com.fishblack.micro.suite.rabbitmq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 *
 */
final public class RabbitMQ {

    private Logger logger = LoggerFactory.getLogger(RabbitMQ.class);

    private Properties properties = null;
    private ExecutorService executorService = null;
    private Connection connection = null;
    private HashMap<Long,Channel> channelHashMap = new HashMap<>();

    public RabbitMQ(Properties props)throws NotificationException {
        validate(props);
        createExecutorService();
        createConnection();
    }

    private void validate(Properties props) throws NotificationException {
        this.properties = props;
        if(StringUtils.isEmpty(properties.getProperty(NotificationConstants.HOSTNAME))){
            new NotificationException("Invalid rabbitmq host.");
        }
        try {
            new InetSocketAddress(Integer.valueOf(properties.getProperty(NotificationConstants.PORT)));
        }catch(IllegalArgumentException ex){
            new NotificationException("Invalid rabbitmq port. "+ex.getMessage());
        }
    }

    private void createExecutorService()throws NotificationException {
        String poolsSizeStr = properties.getProperty(NotificationConstants.THREAD_POOL_SIZE);
        executorService = Executors.newFixedThreadPool(Integer.parseInt(poolsSizeStr));
    }

    private Address[] getConfiguredBrokers(){
        String [] hosts = StringUtils.split(properties.getProperty(NotificationConstants.HOSTNAME),',');
        int port = Integer.valueOf(properties.getProperty(NotificationConstants.PORT));
        Address[] addresses = new Address[hosts.length];
        for(int i = 0; i < hosts.length; i++ ){
            addresses[i] = new Address(hosts[i], port);
        }
        return addresses;
    }

    public void createConnection() throws NotificationException {
        try {
            connection = new ConnectionFactory() { {
                // ensure messages survive rabbitmq restarts
                setAutomaticRecoveryEnabled(true);
                setNetworkRecoveryInterval(NotificationConstants.NETWORK_RECOVERY_INTERVAL);


                setVirtualHost(properties.getProperty(NotificationConstants.VIRTUALHOST));
                setUsername(properties.getProperty(NotificationConstants.USER));
                setPassword(properties.getProperty(NotificationConstants.PASSWORD));
                String useSsl = properties.getProperty(NotificationConstants.USE_SSL);
                if((useSsl == null) || (useSsl.equals("true"))){
                    useSslProtocol();
                }
            }}.newConnection(getConfiguredBrokers());
        } catch (IOException | TimeoutException ex){
            connection = null;
            logger.info("Failed to initialize connection to RabbitMQ: {}. Will attempt to connect latter with: {}"
                    ,ex.getMessage(),properties());
        } catch (NoSuchAlgorithmException | KeyManagementException ex){
            logger.error("Failed to initialize connection to RabbitMQ",ex);
            throw new NotificationException("Failed to initialize connection to RabbitMQ",ex);
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public boolean isConnected(){
        return (connection != null);
    }

    public Properties getProperties(){
        return properties;
    }
    public ExecutorService getExecutorService(){
        return executorService;
    }

    public Channel getChannelFromCache() throws IOException {
        long threadId = Thread.currentThread().getId();
        Channel channel = channelHashMap.get(threadId);
        if((channel == null) && (connection != null)) {
            channel = connection.createChannel();
            channelHashMap.put(threadId,channel);
            logger.debug("Created new channel for thread: " + threadId);
        }
        return channel;
    }

    public void close() throws IOException, TimeoutException {
        executorService.shutdown();
        for(Channel channel : channelHashMap.values()){
            if(channel.isOpen()) {
                channel.close();
            }
        }
        if(connection != null) {
            connection.close();
        }
    }

    public String properties() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        properties.stringPropertyNames().stream().filter(prop -> !"password".equals(prop)).forEach(prop -> {
            String value = properties.getProperty(prop);
            sb.append(" ").append(prop).append("=").append(value);
        });
        sb.append(" }");
        return sb.toString();
    }
}