# web3j Spring Boot Starter

Integrate web3j into your Spring Boot applications via Spring's dependency injection.


## Getting started

A sample application is available [here](https://github.com/web3j/examples/tree/master/spring-boot)

To use, create a new [Spring Boot Application](https://spring.io/guides/gs/spring-boot/), and 
include the following dependencies:

Maven:

```xml
<dependency>
    <groupId>org.web3j</groupId>
    <artifactId>web3j-spring-boot-starter</artifactId>
    <version>1.3.0</version>
</dependency>
```

Gradle:

```groovy
compile ('org.web3j:web3j-spring-boot-starter:1.3.0')
```

Now Spring can inject web3j instances for you where ever you need them:

```java
@Autowired
private Web3j web3j;
```

No additional configuration is required if you want to connect via HTTP to the default URL 
http://localhost:8545.

Otherwise simply add the address of the endpoint in your application properties:

```properties
# An infura endpoint
web3j.client-address = https://rinkeby.infura.io/

# Or, an IPC endpoing
web3j.client-address = /path/to/file.ipc
```


## Admin clients

If you wish to make use of the personal module methods that are common to both
[Parity](https://github.com/ethcore/parity/wiki/JSONRPC-personal-module) and 
[Geth](https://github.com/ethereum/go-ethereum/wiki/Management-APIs#personal)  
to manage accounts, enable the admin client:

```properties
web3j.admin-client = true
```

Then Spring can inject admin clients:

 ```java
 @Autowired
 private Admin parity;
 ```

**Note**: This is not required for transacting with web3j.  


## Further information

For further information on web3j, please refer to the [web3j home page](https://web3j.io).
