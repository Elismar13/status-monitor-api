package com.thushima.statusmonitor.monitor.infrastructure.webclient;

import com.thushima.statusmonitor.monitor.presentation.dto.MonitorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Component
public class StatusCheckerWebClient {

    private final WebClient webClient;

    public StatusCheckerWebClient(WebClient.Builder webClientBuilder) {
        // Configuração do ConnectionProvider com limite de conexões e timeout
        ConnectionProvider connectionProvider = ConnectionProvider.builder("status-checker")
                .maxConnections(100)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofMinutes(5))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(30));

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Cacheable(value = "statusCache", key = "#url", unless = "#result == null || !#result.isUp()")
    public Mono<MonitorResult> checkStatus(String url, int timeoutInSeconds) {
        long startTime = System.currentTimeMillis();

        return webClient.get()
                .uri(url)
                .exchangeToMono(clientResponse -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    int statusCode = clientResponse.statusCode().value();

                    return clientResponse.releaseBody()
                            .then(Mono.just(MonitorResult.up(statusCode, responseTime)));
                })
                .timeout(
                        Duration.ofSeconds(timeoutInSeconds),
                        Mono.defer(() -> Mono.just(MonitorResult.down(0, "Request timeout")))
                )
                .onErrorResume(e -> {
                    log.warn("Error checking status for {}: {}", url, e.getMessage());
                    return Mono.just(MonitorResult.down(0, e.getMessage()));
                })
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
    }
}
