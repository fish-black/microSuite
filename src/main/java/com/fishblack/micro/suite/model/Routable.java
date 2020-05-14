package com.fishblack.micro.suite.model;

import java.io.IOException;

public interface Routable {

    /**
     * Returns the JSON message as a String to be published into the message queue.
     * @return the JSON message to be published
     */
    String getJsonMessage() throws IOException;

    /**
     * Return the routing key to be used to in the published message.
     * @return the routing key
     */
    String getRoutingKey();
}
