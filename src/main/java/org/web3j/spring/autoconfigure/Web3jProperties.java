package org.web3j.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.web3j.spring.autoconfigure.Web3jProperties.WEB3J_PREFIX;

/**
 * web3j property container.
 */
@ConfigurationProperties(prefix = WEB3J_PREFIX)
public class Web3jProperties {

    public static final String WEB3J_PREFIX = "web3j";

    private String clientAddress;

    private Boolean adminClient;

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public Boolean isAdminClient() {
        return adminClient;
    }

    public void setAdminClient(Boolean adminClient) {
        this.adminClient = adminClient;
    }
}
