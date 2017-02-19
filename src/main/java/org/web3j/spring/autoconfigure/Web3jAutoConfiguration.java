package org.web3j.spring.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;
import org.web3j.protocol.parity.Parity;

/**
 * web3j auto configuration for Spring Boot.
 */
@Configuration
@ConditionalOnClass(Web3j.class)
@EnableConfigurationProperties(Web3jProperties.class)
public class Web3jAutoConfiguration {

    private static Log log = LogFactory.getLog(Web3jAutoConfiguration.class);

    @Autowired
    private Web3jProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public Web3j web3j() {
        Web3jService web3jService = buildService(properties.getClientAddress());
        log.info("Building service for endpoint: " + properties.getClientAddress());
        return Web3j.build(web3jService);
    }

    @Bean
    @ConditionalOnProperty(prefix = Web3jProperties.WEB3J_PREFIX, name = "admin-client", havingValue = "true")
    public Parity parity() {
        Web3jService web3jService = buildService(properties.getClientAddress());
        log.info("Building admin service for endpoint: " + properties.getClientAddress());
        return Parity.build(web3jService);
    }

    private Web3jService buildService(String clientAddress) {
        Web3jService web3jService;

        if (clientAddress == null || clientAddress.equals("")) {
            web3jService = new HttpService();
        } else if (clientAddress.contains("infura.io")) {
            web3jService = new InfuraHttpService(clientAddress);
        } else if (clientAddress.startsWith("http")) {
            web3jService = new HttpService(clientAddress);
        } else if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            web3jService = new WindowsIpcService(clientAddress);
        } else {
            web3jService = new UnixIpcService(clientAddress);
        }

        return web3jService;
    }
}
