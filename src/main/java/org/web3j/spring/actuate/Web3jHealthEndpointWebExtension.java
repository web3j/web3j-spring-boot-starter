package org.web3j.spring.actuate;

import java.util.concurrent.CompletableFuture;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.util.Assert;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthProtocolVersion;
import org.web3j.protocol.core.methods.response.NetPeerCount;
import org.web3j.protocol.core.methods.response.NetVersion;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

/**
 * Health endpoint extension for Web3j
 */

@EndpointWebExtension(endpoint = HealthEndpoint.class)
public class Web3jHealthEndpointWebExtension {

	private Web3j web3j;

	public Web3jHealthEndpointWebExtension(Web3j web3j) {
		Assert.notNull(web3j, "Web3j must not be null");
		this.web3j = web3j;
	}

	@ReadOperation
	public Health health() {
		Builder builder = Health.up();

		try {
			boolean listening = web3j.netListening().send().isListening();
			if (!listening) {
				builder = builder.down();
			} else {
				CompletableFuture<NetVersion> netVersion = web3j.netVersion().sendAsync();
				CompletableFuture<Web3ClientVersion> clientVersion = web3j.web3ClientVersion().sendAsync();
				CompletableFuture<EthBlockNumber> ethBlockNumber = web3j.ethBlockNumber().sendAsync();
				CompletableFuture<EthProtocolVersion> ethProtocolVersion = web3j.ethProtocolVersion().sendAsync();
				CompletableFuture<NetPeerCount> netPeerCount = web3j.netPeerCount().sendAsync();
				CompletableFuture.allOf(netVersion, clientVersion, ethBlockNumber, ethProtocolVersion, netPeerCount)
						.join();

				builder.withDetail("netVersion", netVersion.get().getNetVersion());
				builder.withDetail("clientVersion", clientVersion.get().getWeb3ClientVersion());
				builder.withDetail("blockNumber", ethBlockNumber.get().getBlockNumber());
				builder.withDetail("protocolVersion", ethProtocolVersion.get().getProtocolVersion());
				builder.withDetail("netPeerCount", netPeerCount.get().getQuantity());
			}
		} catch (Exception e) {
			builder = builder.down(e);
		}

		return builder.build();
	}
}
