package com.fishblack.micro.suite.rabbitmq.consumer;

import java.io.IOException;

/**
 * Implement this interface in order to be notified of received events.
 */
public interface NotificationListener {

    public void handleMessage(String jsonMsg) throws IOException;

}
