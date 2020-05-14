package com.fishblack.micro.suite.rabbitmq.utils;

import com.google.common.io.Files;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

/**
 * Embedded AMQP broker. Used for unit tests.
 */
public class EmbeddedAMQPBroker {

    public static final int BROKER_PORT = 5454;
    private final Broker broker = new Broker();


    /**
     * Creates and starts an embedded AMQP broker
     * @param configFileName
     * @param passwordFileName
     * @throws Exception
     */
    public EmbeddedAMQPBroker(final String configFileName, final String passwordFileName) throws Exception {
        final BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", String.valueOf(BROKER_PORT));
        brokerOptions.setConfigProperty("qpid.pass_file", passwordFileName);
        brokerOptions.setConfigProperty("qpid.work_dir", Files.createTempDir().getAbsolutePath());
        brokerOptions.setInitialConfigurationLocation(configFileName);
        broker.startup(brokerOptions);
    }

    /**
     * Stops the broker.
     */
    public void stop(){
        try {
            broker.shutdown();
        }catch (Throwable th){
            //ignore : as this is used only for unit tests.
        }

    }
}