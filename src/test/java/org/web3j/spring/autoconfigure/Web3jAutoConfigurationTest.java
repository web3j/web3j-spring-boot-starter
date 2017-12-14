package org.web3j.spring.autoconfigure;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import org.web3j.protocol.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.http.HttpService;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Web3jAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @After
    public void tearDown() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void testEmptyClientAddress() throws Exception {
        verifyHttpConnection("", HttpService.DEFAULT_URL, HttpService.class);
    }

    @Test
    public void testHttpClient() throws Exception {
        verifyHttpConnection(
                "https://localhost:12345", HttpService.class);
    }

    @Test
    public void testUnixIpcClient() throws IOException {
        Path path = Files.createTempFile("unix", "ipc");
        path.toFile().deleteOnExit();

        load(EmptyConfiguration.class, "web3j.client-address=" + path.toString());
    }

    @Test
    public void testWindowsIpcClient() throws IOException {
        // Windows uses a RandomAccessFile to access the named pipe, hence we can initialise
        // the WindowsIPCService in web3j
        Path path = Files.createTempFile("windows", "ipc");
        path.toFile().deleteOnExit();

        System.setProperty("os.name", "windows");
        load(EmptyConfiguration.class, "web3j.client-address=" + path.toString());
    }

    @Test
    public void testAdminClient() {
        load(EmptyConfiguration.class, "web3j.client-address=", "web3j.admin-client=true");

        this.context.getBean(Admin.class);
        try {
            this.context.getBean(Web3j.class);
            fail();
        } catch (NoSuchBeanDefinitionException e) {
        }
    }

    @Test
    public void testNoAdminClient() {
        load(EmptyConfiguration.class, "web3j.client-address=");

        this.context.getBean(Web3j.class);
        try {
            this.context.getBean(Admin.class);
            fail();
        } catch (NoSuchBeanDefinitionException e) {
        }
    }


    @Test
    public void testHealthCheckIndicatorDown() {
        load(EmptyConfiguration.class, "web3j.client-address=");

        HealthIndicator web3jHealthIndicator = this.context.getBean(HealthIndicator.class);
        Health health = web3jHealthIndicator.health();
        assertThat(health.getStatus(), equalTo(Status.DOWN));
        assertThat(health.getDetails().get("error").toString(),
                startsWith("java.net.ConnectException: Failed to connect to localhost/"));
    }

    private void verifyHttpConnection(
            String clientAddress, Class<? extends Service> cls) throws Exception {
        verifyHttpConnection(clientAddress, clientAddress, cls);
    }

    private void verifyHttpConnection(
            String clientAddress, String expectedClientAddress, Class<? extends Service> cls)
            throws Exception {
        load(EmptyConfiguration.class, "web3j.client-address=" + clientAddress);
        Web3j web3j = this.context.getBean(Web3j.class);

        Field web3jServiceField = JsonRpc2_0Web3j.class.getDeclaredField("web3jService");
        web3jServiceField.setAccessible(true);
        Web3jService web3jService = (Web3jService) web3jServiceField.get(web3j);

        assertTrue(cls.isInstance(web3jService));

        Field urlField = HttpService.class.getDeclaredField("url");
        urlField.setAccessible(true);
        String url = (String) urlField.get(web3jService);

        assertThat(url, equalTo(expectedClientAddress));
    }

    @Configuration
    static class EmptyConfiguration {
    }

    private void load(Class<?> config, String... environment) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(applicationContext, environment);
        applicationContext.register(config);
        applicationContext.register(Web3jAutoConfiguration.class);
        applicationContext.refresh();
        this.context = applicationContext;
    }

}
