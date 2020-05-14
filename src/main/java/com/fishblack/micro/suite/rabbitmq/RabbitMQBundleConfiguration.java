package com.fishblack.micro.suite.rabbitmq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.internal.util.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import com.rabbitmq.client.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RabbitMQBundleConfiguration {

    @NotEmpty
    @JsonProperty
    private String host;

    @NotEmpty
    @JsonProperty
    private String port;

    @NotEmpty
    @JsonProperty
    private String virtualHost;

    @NotEmpty
    @JsonProperty
    private String username;

    @NotEmpty
    @JsonProperty
    private String password;

    @JsonProperty
    private String consumeExchange;

    @JsonProperty
    private String publishExchange;

    @JsonProperty
    private int prefetchCount;

    @JsonProperty
    private String consumerThreadPoolSize;

    @JsonProperty
    private String publishThreadPoolSize;

    @JsonProperty
    private String publishAcks;

    @JsonProperty
    private String consumeAcks;

    @JsonProperty
    private Long connectRetryInterval;

    @JsonProperty
    private boolean useSsl = true;

    protected final Log logger = LogFactory.getLog(this.getClass());

    private volatile Address[] addresses;
    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConsumerThreadPoolSize() {
        return consumerThreadPoolSize;
    }

    public int getPrefetchCount() {
        return prefetchCount;
    }

    public String getConsumeExchange() {
        return consumeExchange;
    }

    public String getPublishExchange() {
        return publishExchange;
    }

    public String getPublishThreadPoolSize() {
        return publishThreadPoolSize;
    }

    public String getPublishAcks() {
        return publishAcks;
    }

    public String getConsumeAcks() {
        return consumeAcks;
    }

    public Long getConnectRetryInterval() {
        return connectRetryInterval;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setAddresses(String addresses) {
        if(StringUtils.hasText(addresses)) {
            Address[] addressArray = Address.parseAddresses(addresses);
            if(addressArray.length > 0) {
                this.addresses = addressArray;
                return;
            }
        }

        this.logger.info("setAddresses() called with an empty value, will be using the host+port properties for connections");
        this.addresses = null;
    }
}
