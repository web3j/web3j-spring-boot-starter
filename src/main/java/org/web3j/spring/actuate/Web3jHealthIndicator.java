package org.web3j.spring.actuate;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

/**
 * Health check indicator for Web3j
 */
public class Web3jHealthIndicator extends AbstractHealthIndicator {

    private Web3j web3j;

    public Web3jHealthIndicator(Web3j web3j) {
        Assert.notNull(web3j, "Web3j must not be null");
        this.web3j = web3j;
    }


    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {

            String netVersion = web3j.netVersion().send().getNetVersion();
            if (StringUtils.isEmpty(netVersion)) {
                builder.down();
            } else {
                String web3ClientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
                BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
                String protocolVersion = web3j.ethProtocolVersion().send().getProtocolVersion();
                BigInteger netPeerCount = web3j.netPeerCount().send().getQuantity();
                builder.up().withDetail("netVersion", netVersion)
                            .withDetail("web3ClientVersion", web3ClientVersion)
                            .withDetail("protocolVersion", protocolVersion)
                            .withDetail("netPeerCount", netPeerCount)
                            .withDetail("blockNumber", blockNumber);
            }

        } catch (Exception ex) {
            builder.down(ex);
        }

    }
}
